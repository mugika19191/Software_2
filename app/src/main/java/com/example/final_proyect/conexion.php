<?php

$hostname='localhost';
$database='Ximugica037_';
$username='Ximugica037';
$password='r3AiXeur9';
$conexion=new mysqli($hostname, $username, $password, $database);
if($conexion->connect_errno){
    echo "El sitio web está experimentado problemas";
}
?>