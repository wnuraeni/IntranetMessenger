<?php
include 'koneksi.php';

$username= $_POST['username'];
$userid = $_POST['userid'];
$query = mysql_query("UPDATE `users` SET `username` = '$username' WHERE `id`='$userid'") or die(mysql_error());

?>