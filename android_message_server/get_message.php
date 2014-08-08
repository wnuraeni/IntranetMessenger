<?php
include 'koneksi.php';

$receiver = $_GET['receiver'];
//$sender = $_GET['sender'];
//$query = mysql_query("SELECT message.id_message,message.sender,message.receiver,message.date_time,message.message,message.status,users.id,users.username FROM `message`,`users` WHERE `receiver` ='$receiver' AND `status`='0'") or die(mysql_error());

$query = mysql_query("SELECT id_message,sender,receiver, (SELECT username from users WHERE id=sender)as sendername, (SELECT username from users WHERE id=receiver)as receivername,date_time,status,message FROM `message` WHERE `receiver` = '$receiver' AND `status`='0'");


$data = array();
while($result = mysql_fetch_assoc($query)){
	array_push($data,$result);
	
}
echo json_encode($data);
?>