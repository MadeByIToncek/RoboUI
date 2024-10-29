-- keystore definition
CREATE TABLE `keystore` (`key` varchar(100) NOT NULL,`value` varchar(255) NOT NULL,PRIMARY KEY (`key`)) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_czech_ci;
-- teams definition
CREATE TABLE `teams` (`team_id` int(10) unsigned NOT NULL AUTO_INCREMENT,`team_name` varchar(100) NOT NULL,PRIMARY KEY (`team_id`)) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf16 COLLATE=utf16_czech_ci;
-- members definition
CREATE TABLE `members` (`member_id` int(10) unsigned NOT NULL AUTO_INCREMENT,`firstname` varchar(100) NOT NULL,`surname` varchar(100) NOT NULL,`team_id` int(10) unsigned NOT NULL,PRIMARY KEY (`member_id`),UNIQUE KEY `members_unique` (`firstname`,`surname`),KEY `members_teams_fk` (`team_id`),CONSTRAINT `members_teams_fk` FOREIGN KEY (`team_id`) REFERENCES `teams` (`teamid`)) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_czech_ci;
-- matches definition
CREATE TABLE `matches` (`match_id` int(10) unsigned NOT NULL AUTO_INCREMENT,`team_l` int(10) unsigned NOT NULL,`team_r` int(10) unsigned NOT NULL,`points_left` int(10) unsigned NOT NULL DEFAULT 0,`points_right` int(10) unsigned NOT NULL DEFAULT 0,PRIMARY KEY (`match_id`),KEY `matches_teams_fk` (`team_l`),KEY `matches_teams_fk_1` (`team_r`),CONSTRAINT `matches_teams_fk` FOREIGN KEY (`team_l`) REFERENCES `teams` (`teamid`),CONSTRAINT `matches_teams_fk_1` FOREIGN KEY (`team_r`) REFERENCES `teams` (`teamid`)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf16 COLLATE=utf16_czech_ci;
-- Updating migration status
INSERT INTO robosoutez.keystore (`key`,value) VALUES ('MIGRATION_LEVEL','1');