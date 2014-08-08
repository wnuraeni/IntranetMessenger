<?php

include 'koneksi.php';

$username = $_POST['username'];
$phone = $_POST['phone'];

$query = mysql_query("SELECT * FROM users WHERE username = '$username' AND phone='$phone' ") or die(mysql_error());

$row = mysql_num_rows($query);

$result = mysql_fetch_assoc($query);
$username = $result['username'];
$userid = $result['id'];

if($row==1){
mysql_query("UPDATE users SET status_online = '1' WHERE id='$userid'");

	echo json_encode(array("response"=>"success","username"=>$username,"userid"=>$userid));

}
else{
	echo json_encode(array("response"=>"user not exist"));
}
?>