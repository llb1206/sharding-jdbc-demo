/*
 Navicat Premium Data Transfer

 Source Server         : shardingJdbc
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : orderdb1

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 25/06/2021 21:01:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `dict_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典编码',
  `dict_value` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (124, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1406665970518392834, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1406994746007695362, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1406994941374128130, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1406995041844465665, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1407002888779075586, 'Y', '正常');
INSERT INTO `sys_dict` VALUES (1407368452877889537, 'Y', '正常');

-- ----------------------------
-- Table structure for test_user
-- ----------------------------
DROP TABLE IF EXISTS `test_user`;
CREATE TABLE `test_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1407368168185307137 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_user
-- ----------------------------
INSERT INTO `test_user` VALUES (1, 'sharding-5', 'pw5');
INSERT INTO `test_user` VALUES (1406624515724726274, 'sharding-3', 'pw3');
INSERT INTO `test_user` VALUES (1406624516035104770, 'sharding-4', 'pw4');
INSERT INTO `test_user` VALUES (1406994317534314497, 'sharding-1', 'pw1');
INSERT INTO `test_user` VALUES (1406994320600350721, 'sharding-3', 'pw3');
INSERT INTO `test_user` VALUES (1406998157042126849, 'sharding-1', 'pw1');
INSERT INTO `test_user` VALUES (1406998159793590273, 'sharding-2', 'pw2');
INSERT INTO `test_user` VALUES (1406998160187854849, 'sharding-3', 'pw3');
INSERT INTO `test_user` VALUES (1406998161051881473, 'sharding-5', 'pw5');
INSERT INTO `test_user` VALUES (1407368163202473986, 'sharding-1', 'pw1');
INSERT INTO `test_user` VALUES (1407368167052845057, 'sharding-2', 'pw2');
INSERT INTO `test_user` VALUES (1407368167455498241, 'sharding-3', 'pw3');
INSERT INTO `test_user` VALUES (1407368167866540034, 'sharding-4', 'pw4');
INSERT INTO `test_user` VALUES (1407368168185307137, 'sharding-5', 'pw5');

-- ----------------------------
-- Table structure for test_user_0
-- ----------------------------
DROP TABLE IF EXISTS `test_user_0`;
CREATE TABLE `test_user_0`  (
  `id` bigint NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_user_0
-- ----------------------------

-- ----------------------------
-- Table structure for test_user_1
-- ----------------------------
DROP TABLE IF EXISTS `test_user_1`;
CREATE TABLE `test_user_1`  (
  `id` bigint NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_user_1
-- ----------------------------
INSERT INTO `test_user_1` VALUES (1406623649340858369, 'sharding-1', 'pw1');
INSERT INTO `test_user_1` VALUES (1406623651719028737, 'sharding-2', 'pw2');
INSERT INTO `test_user_1` VALUES (1406623652016824321, 'sharding-3', 'pw3');
INSERT INTO `test_user_1` VALUES (1406623652427866113, 'sharding-4', 'pw4');
INSERT INTO `test_user_1` VALUES (1406624513162006529, 'sharding-1', 'pw1');
INSERT INTO `test_user_1` VALUES (1406624515418542081, 'sharding-2', 'pw2');
INSERT INTO `test_user_1` VALUES (1406624516450340865, 'sharding-5', 'pw5');
INSERT INTO `test_user_1` VALUES (1406664770569908225, 'sharding-1', 'pw1');
INSERT INTO `test_user_1` VALUES (1406664773384286209, 'sharding-2', 'pw2');
INSERT INTO `test_user_1` VALUES (1406664774516748289, 'sharding-5', 'pw5');
INSERT INTO `test_user_1` VALUES (1406665015819272193, 'sharding-2', 'pw2');

SET FOREIGN_KEY_CHECKS = 1;
