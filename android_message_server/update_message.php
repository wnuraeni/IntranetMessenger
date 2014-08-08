<?php
include 'koneksi.php';

$msg_id= $_GET['msg_id'];
$query = mysql_query("UPDATE `message` SET `status` = '1' WHERE `id_message`='$msg_id'") or die(mysql_error());

?>