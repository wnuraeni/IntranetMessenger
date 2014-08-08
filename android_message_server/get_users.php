<?php
include 'koneksi.php';

$except = $_GET['except'];
$query = mysql_query("SELECT * FROM `users` WHERE id != '$except'") or die(mysql_error());
$data = array();
while($result = mysql_fetch_assoc($query)){
	array_push($data,$result);
}
echo json_encode($data);
?>