CREATE TABLE `user`
(
    `id`           bigint(13) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(30) NOT NULL DEFAULT '',
    `gmt_create`   bigint(13) NOT NULL DEFAULT '0',
    `gmt_modified` bigint(13) NOT NULL DEFAULT '0',
    `deleted`      tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
)
