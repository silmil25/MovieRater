

CREATE DATABASE `movierater` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;

CREATE TABLE `movies` (
                          `movie_id` int(11) NOT NULL AUTO_INCREMENT,
                          `title` varchar(100) NOT NULL,
                          `director` varchar(50) DEFAULT NULL,
                          `release_year` int(11) DEFAULT NULL,
                          `rating` float DEFAULT NULL,
                          UNIQUE KEY `movies_pk` (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `users` (
                         `user_id` int(11) NOT NULL AUTO_INCREMENT,
                         `username` varchar(30) NOT NULL,
                         `password` varchar(100) NOT NULL,
                         `is_admin` tinyint(1) NOT NULL DEFAULT 0,
                         PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

