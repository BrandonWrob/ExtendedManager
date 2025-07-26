CREATE TABLE `Table: users`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name (Not Null, Unique)` VARCHAR(255) NOT NULL,
    `username (Not Null, Unique)` VARCHAR(255) NOT NULL,
    `email (Not Null, Unique)` VARCHAR(255) NOT NULL,
    `password (Not Null, Unique)` VARCHAR(255) NOT NULL
);
CREATE TABLE `Table: roles`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name (Not Null, Unique)` VARCHAR(255) NOT NULL
);
CREATE TABLE `Join-Table: users_roles`(
    `user_id (Not Null)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `role_id (Not Null)` BIGINT NOT NULL
);
CREATE TABLE `Table: ingredient`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `amount` INT NOT NULL
);
CREATE TABLE `Table: inventory`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY
);
CREATE TABLE `Join-Table: inventory_ingredient`(
    `inventory_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `ingredient_id` BIGINT NOT NULL
);
CREATE TABLE `Table: recipes`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` CHAR(255) NOT NULL,
    `price` INT NOT NULL
);
CREATE TABLE `Join-Table: recipes_ingredient`(
    `recipes_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `ingredient_id` BIGINT NOT NULL
);
CREATE TABLE `Table: multi_recipes`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` CHAR(255) NOT NULL,
    `price` INT NOT NULL,
    `amount` INT NOT NULL
);
CREATE TABLE `Join-Table: multi_recipes_ingredient`(
    `multi_recipes_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `ingredient_id` BIGINT NOT NULL
);
CREATE TABLE `orders`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `fulfilled` BOOLEAN NOT NULL
);
CREATE TABLE `Join-Table: orders_multi_recipes`(
    `orders_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `multi_recipes_id` BIGINT NOT NULL
);
CREATE TABLE `order_history`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `picked_up` BOOLEAN NOT NULL,
    `recipes_in_order` CHAR(255) NOT NULL,
    `ingredients_used` CHAR(255) NOT NULL,
    `total` DOUBLE NOT NULL,
    `username` CHAR(255) NOT NULL
);
CREATE TABLE `tax`(
    `id (Auto_Inc)` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `rate` DOUBLE NOT NULL
);
ALTER TABLE
    `Join-Table: multi_recipes_ingredient` ADD CONSTRAINT `join_table: multi_recipes_ingredient_ingredient_id_foreign` FOREIGN KEY(`ingredient_id`) REFERENCES `Table: ingredient`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: users_roles` ADD CONSTRAINT `join_table: users_roles_role_id (not null)_foreign` FOREIGN KEY(`role_id (Not Null)`) REFERENCES `Table: roles`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: recipes_ingredient` ADD CONSTRAINT `join_table: recipes_ingredient_ingredient_id_foreign` FOREIGN KEY(`ingredient_id`) REFERENCES `Table: recipes`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: recipes_ingredient` ADD CONSTRAINT `join_table: recipes_ingredient_recipes_id_foreign` FOREIGN KEY(`recipes_id`) REFERENCES `Table: ingredient`(`id (Auto_Inc)`);
ALTER TABLE
    `Table: multi_recipes` ADD CONSTRAINT `table: multi_recipes_id (auto_inc)_foreign` FOREIGN KEY(`id (Auto_Inc)`) REFERENCES `Join-Table: multi_recipes_ingredient`(`multi_recipes_id`);
ALTER TABLE
    `Join-Table: orders_multi_recipes` ADD CONSTRAINT `join_table: orders_multi_recipes_orders_id_foreign` FOREIGN KEY(`orders_id`) REFERENCES `orders`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: users_roles` ADD CONSTRAINT `join_table: users_roles_user_id (not null)_foreign` FOREIGN KEY(`user_id (Not Null)`) REFERENCES `Table: users`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: inventory_ingredient` ADD CONSTRAINT `join_table: inventory_ingredient_inventory_id_foreign` FOREIGN KEY(`inventory_id`) REFERENCES `Table: inventory`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: inventory_ingredient` ADD CONSTRAINT `join_table: inventory_ingredient_ingredient_id_foreign` FOREIGN KEY(`ingredient_id`) REFERENCES `Table: ingredient`(`id (Auto_Inc)`);
ALTER TABLE
    `Join-Table: orders_multi_recipes` ADD CONSTRAINT `join_table: orders_multi_recipes_multi_recipes_id_foreign` FOREIGN KEY(`multi_recipes_id`) REFERENCES `Table: multi_recipes`(`id (Auto_Inc)`);
ALTER TABLE
    `orders` ADD CONSTRAINT `orders_id (auto_inc)_foreign` FOREIGN KEY(`id (Auto_Inc)`) REFERENCES `Table: users`(`id (Auto_Inc)`);