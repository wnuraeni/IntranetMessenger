<?php
include 'koneksi.php';

    $base=$_REQUEST['picture'];
    $userid = $_REQUEST['userid'];
    $binary=base64_decode($base);
    //header('Content-Type: bitmap; charset=utf-8');
    $filename = $userid.'.jpg';
    $file = fopen('./images/'.$filename, 'wb');
    fwrite($file, $binary);
    fclose($file);

    $query = mysql_query("UPDATE `users` SET `picture`='$filename' WHERE `id`='$userid'") or die(mysql_error());

echo json_encode(array("response"=>$query));
?>