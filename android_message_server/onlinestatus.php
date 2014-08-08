<?php
include 'koneksi.php';

$userid= $_GET['userid'];
$status = $_GET['status'];

$query = mysql_query("UPDATE `users` SET `status_online` = '$status' WHERE `id`='$userid'") or die(mysql_error());
?>