<?php
include 'koneksi.php';

$username = $_POST['username'];
$phone = $_POST['phone'];
//$username = "saya";


$query = mysql_query("SELECT * FROM `users` WHERE `username`='$username'") or die(mysql_error());

$row = mysql_num_rows($query);
if($row > 0){
	echo json_encode(array("username"=>"exist"));
}else{
	$query = mysql_query("INSERT INTO `users` VALUES (NULL,'$username','$phone','1','default.jpg')") or die(mysql_error());
$id = mysql_insert_id();

echo json_encode(array("username"=>$username,"userid"=>$id));
}

?>