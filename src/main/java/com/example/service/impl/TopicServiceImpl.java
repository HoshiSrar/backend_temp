package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.request.TopicUpdateVo;
import com.example.entity.vo.response.CommentVo;
import com.example.entity.vo.response.TopTopicVo;
import com.example.entity.vo.response.TopicDetailVo;
import com.example.entity.vo.response.TopicPreviewVo;
import com.example.mapper.TopicMapper;
import com.example.service.*;
import com.example.utils.BeanCopyUtils;
import com.example.utils.CacheUtils;
import com.example.utils.FlowUtils;
import com.example.utils.constants.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * (Topic)表服务实现类
 *
 * @author makejava
 * @since 2024-01-04 21:08:44
 */
@Service("topicService")
@Slf4j
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeService topicTypeService;
    @Resource
    FlowUtils flowUtils;
    @Resource
    CacheUtils cacheUtils;
    @Resource
    UserService userService;
    @Resource
    UserAvatarService userAvatarService;
    @Resource
    UserDetailsService userDetailsService;
    @Resource
    UserPrivacyService userPrivacyService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    TopicCommentService topicCommentService;
    @Resource
    NotificationService notificationService;

    @Override
    public String createTopic(int uid, TopicCreateVo topicVo) {
        if (!textLimitCheck(topicVo.getContent(),20000)) {
            return "文章内容过多，发布失败";
        }
        Set<Integer> types = topicTypeService.listType().stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());

        if (!types.contains(Integer.parseInt(topicVo.getType()))){
            return "文章类型非法";
        }
        // 每小时只能发3篇
        boolean check = flowUtils.limitPeriodCounterCheck(SystemConstants.FORUM_TOPIC_COUNTER, 3, 3600);
        if (!check){
            return "发文达到上限，请稍后再试";
        }
        Topic topic = BeanCopyUtils.copyBean(topicVo, Topic.class);
        topic.setContent(topicVo.getContent().toJSONString());
        topic.setUid(uid);
        try {
            if (save(topic)){
                cacheUtils.deleteCachePattern(SystemConstants.FORUM_TOPIC_PREVIEW_CACHE + "*");
                return null;
            }
        }catch (Exception e){
            log.info("保存帖子数据库出错，[信息：{}，原因：{}]",e.getMessage(),e.getCause());
            return "出现错误，请联系管理员";
        }return "内部错误，请联系管理员";
    }

    @Override
    public List<TopicPreviewVo> listTopicByPage(int pageNumber, int type) {
        String key = SystemConstants.FORUM_TOPIC_PREVIEW_CACHE + pageNumber +":"+type;
        List<TopicPreviewVo> list = cacheUtils.takeListFromCache(key, TopicPreviewVo.class);
        if (list != null) return list;
        List<Topic> topics;
        Page<Topic> page = Page.of(pageNumber, 10);
        if (type == 0) {
            baseMapper.selectPage(page, Wrappers.<Topic>query().orderByDesc("time"));
        }else {
            baseMapper.selectPage(page,Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));
        }
        topics = page.getRecords();
        if (topics.isEmpty()) return null;
        list = topics.stream().map(topic -> resolveToPreview(topic)).toList();
        cacheUtils.saveListToCache(key,list,60);
        return list;
    }

    @Override
    public List<TopTopicVo> listTopTopics() {
        List<Topic> topics = query().
                select("id","title","top")
                .eq("top", '1')
                .list();
        return topics.stream().map(topic -> BeanCopyUtils.copyBean(topic, TopTopicVo.class)).toList();
    }

    @Override
    public TopicDetailVo getTopic(int tid, int uid) {
        Topic topic = getById(tid);
        TopicDetailVo vo = BeanCopyUtils.copyBean(topic, TopicDetailVo.class);
        TopicDetailVo.User user = new TopicDetailVo.User();
        TopicDetailVo.Interact interact = new TopicDetailVo.Interact(
                hasInteract(tid,uid,"like"),
                hasInteract(tid,uid,"collect"));
        vo.setInteract(interact);
        vo.setType(Integer.valueOf(topic.getType()));
        user.setAvatar(userAvatarService.getById(topic.getUid()).getAvatar());
        vo.setUser(findUserDetailsByPrivacy(user, topic.getUid()));
        vo.setComments(topicCommentService.count(Wrappers.<TopicComment>query().eq("tid", tid)));
        return vo;
    }

    private boolean hasInteract(int tid,int uid,String type){
        String key = tid + ":" +uid;
        if(stringRedisTemplate.opsForHash().hasKey(type,key)){
            return Boolean.parseBoolean(stringRedisTemplate.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid,uid, type) > 0;
    }

    /**
     * 常用交互的更新（入点赞，收藏等），需要使用 redis 进行缓存
     * 当数据到来时，创建一个新的定时任务隔一段时间将 redis 缓存的
     * 相关信息一次性传入数据库中，在其他时间仅更新 redis 中的数据
     * @param interact
     * @param state
     */
    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()){
            stringRedisTemplate.opsForHash().put(type,interact.toKey(),String.valueOf(state));
            saveInteractSchedule(type);
        }
    }

    /**
     * 用户收藏的帖子
     * @param uid 用户uid
     * @return 显示的帖子信息
     */
    @Override
    public List<TopicPreviewVo> listTopicCollect(int uid) {
        return  baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVo vo = BeanCopyUtils.copyBean(topic, TopicPreviewVo.class);
                    return vo;
                }).toList();
    }

    @Override
    public String updateTopic(TopicUpdateVo vo, int uid) {
        if (!textLimitCheck(vo.getContent(),20000)){
            return "文章内容太多，发文失败";
        }
        Set<Integer> types = topicTypeService.listType().stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());

        if (!types.contains(vo.getType())){
            return "文章类型非法";
        }
        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id",vo.getId())
                .set("content", vo.getContent().toString())
                .set("title",vo.getTitle())
                .set("type", vo.getType()));
        return null;
    }

    @Override
    public String createComment(int uid, AddCommentVo vo) {
        if (!textLimitCheck(JSONObject.parseObject(vo.getContent()),200)){
            return "评论内容太多，发文失败";
        }
        String key = SystemConstants.FORUM_TOPIC_COMMENT + uid;
        // 每分钟只能发2条评论
        boolean check = flowUtils.limitPeriodCounterCheck(key, 2, 60);
        if (!check){
            return "发表评论频繁，请稍后再试";
        }
        TopicComment comment = BeanCopyUtils.copyBean(vo, TopicComment.class).setUid(uid);
        topicCommentService.save(comment);
        Topic topic = getById(vo.getTid());
        User user = userService.getById(uid);
        if (vo.getQuote() > 0){
            TopicComment com = topicCommentService.getById(vo.getQuote());
            if (!Objects.equals(user.getId(), com.getUid())){
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的帖子评论回复",
                user.getUsername()+"回复了你的评论，快去看看吧",
                        "success",
                        "/index/topic-detail/"+com.getTid()
                );
            }
        }else if (!Objects.equals(user.getId(), topic.getUid())){
            notificationService.addNotification(topic.getUid(),
                    "您有新的帖子回复",
                    user.getUsername()+"回复了你发表的主题："+topic.getTitle()+"，快去看看吧",
                    "success",
                    "/index/topic-detail/"+topic.getId()
            );
        }
        return null;
    }

    @Override
    public List<CommentVo> comments(int tid, int pageNumber) {
        Page<TopicComment> commentPage = Page.of(pageNumber,10);
        topicCommentService.page(commentPage, Wrappers.<TopicComment>query().eq("tid", tid));
        return commentPage.getRecords().stream().map((dto)->{
            CommentVo vo = BeanCopyUtils.copyBean(dto, CommentVo.class);
            if (dto.getQuote() > 0){
                TopicComment comment = topicCommentService.getOne(Wrappers.<TopicComment>query()
                        .eq("id", dto.getQuote()).orderByAsc("time"));
                if (comment != null){
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    shortContent(object.getJSONArray("ops"),builder,ignore->{});
                    vo.setQuote(builder.toString());
                }else {
                    vo.setQuote("此评论已被删除");
                }
            }
            CommentVo.User user= new CommentVo.User();
            findUserDetailsByPrivacy(user, dto.getUid());
            user.setAvatar(userAvatarService.getById(dto.getUid()).getAvatar());
            vo.setUser(user);
            return vo;
        }).toList();
    }

    @Override
    public void deleteComment(int id, int uid) {
        topicCommentService.remove(Wrappers.<TopicComment>query().eq("uid", uid).eq("id", id));
    }

    private final Map<String,Boolean> state = new HashMap<>();
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    /**
     * 定时执行保存数据库任务
     * @param type： 保存的 Interact 类型
     */
    private void saveInteractSchedule(String type){
        if (!state.getOrDefault(type, false)){
            state.put(type, true);
            service.schedule(()->{
                saveInteract(type);
                state.put(type,false);
            },3, TimeUnit.SECONDS);
        }

    }

    /**
     * 保存 interact 操作进数据库中
     * @param type
     */
    private void saveInteract(String type){
        synchronized (type.intern()){
            List<Interact> check = new ArrayList<>();
            List<Interact> uncheck = new ArrayList<>();
            stringRedisTemplate.opsForHash().entries(type)
                    .forEach((k,v)->{
                        if (Boolean.parseBoolean(v.toString()))
                            check.add(Interact.parseInteract((String) k,type));
                        else
                            uncheck.add(Interact.parseInteract((String) k,type));
                    });
            if (!check.isEmpty()){
                baseMapper.addInteract(check, type);
            }
            if (!uncheck.isEmpty()){
                baseMapper.deleteInteract(uncheck,type);
            }
            stringRedisTemplate.delete(type);
        }
    }

    /**
     * 配置 对应 uid 的用户的隐私信息
     * @param target
     * @param uid
     * @return
     * @param <T>
     */
    private <T> T findUserDetailsByPrivacy(T target,int uid){
        UserDetails details = userDetailsService.getById(uid);
        User user = userService.getById(uid);
        UserPrivacy userPrivacy = userPrivacyService.getById(uid);
        String[] PrivacyIgnoreFields = userPrivacy.hiddenFields();
        BeanUtils.copyProperties(user, target,PrivacyIgnoreFields);
        BeanUtils.copyProperties(details, target,PrivacyIgnoreFields);
        return target;
    }

    private TopicPreviewVo resolveToPreview(Topic topic){
        TopicPreviewVo vo;
        vo = BeanCopyUtils.copyBean(topic,TopicPreviewVo.class);
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        shortContent(ops, previewText, obj -> images.add(obj.toString()));

        vo.setText(previewText.length() > 300 ? previewText.substring(0,300):previewText.toString());
        vo.setImages(images);
        vo.setTime(new Date());
        User user = userService.getById(topic.getUid());
        vo.setUsername(user.getUsername());
        vo.setAvatar(userAvatarService.getById(user.getId()).getAvatar());
        return vo;
    }

    /**
     * 缩短文本传入的 JSONArray ops 文本，由 builder 接收
     * @param ops
     * @param previewText
     * @param imageHandler
     */
    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler){
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text){
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }


    private boolean textLimitCheck(JSONObject jSONText, int mix){
        if (jSONText == null) return false;
        long length = 0;
        for (Object op : jSONText.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > mix) return false;
        }
        return true;
    }
}
