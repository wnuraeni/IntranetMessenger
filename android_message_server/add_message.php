<?php
include 'koneksi.php';

$sender = $_POST['sender'];
$receiver = $_POST['receiver'];
$datetime = $_POST['date_time'];
$message = $_POST['message'];
$status = $_POST['status'];

$query = mysql_query("INSERT INTO `message` VALUES (NULL, '$sender', '$receiver', '$datetime', '$message', '$status')") or die(mysql_error());
?>