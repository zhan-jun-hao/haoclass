# 本地测试视频

当前测试视频：

```text
/videos/haoclass-demo.mp4
```

本地联调时，把 `course_episode.videoUrl` 填成上面的相对路径即可。

示例：

```sql
UPDATE course_episode
SET videoUrl = '/videos/haoclass-demo.mp4',
    durationSeconds = 5
WHERE id = 你的章节ID;
```
