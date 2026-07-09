CREATE TABLE `course_episode` (
                                  `id` bigint unsigned NOT NULL COMMENT '章节ID',
                                  `courseId` bigint unsigned NOT NULL COMMENT '课程ID',
                                  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节标题',
                                  `videoUrl` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '视频地址',
                                  `durationSeconds` int unsigned NOT NULL DEFAULT '0' COMMENT '视频时长，单位：秒',
                                  `freePreview` tinyint NOT NULL DEFAULT '0' COMMENT '是否试看：0否 1是',
                                  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
                                  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0草稿 1上架 2下架',
                                  `createdUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '创建人ID',
                                  `updatedUser` bigint unsigned NOT NULL DEFAULT '0' COMMENT '更新人ID',
                                  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除 1已删除',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_course_admin` (`courseId`,`deleted`,`sort`),
                                  KEY `idx_course_client` (`courseId`,`deleted`,`status`,`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';