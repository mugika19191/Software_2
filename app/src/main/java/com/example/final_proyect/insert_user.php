<?php
include 'conexion.php';
$usu_usuario=$_POST['usuario'];
$usu_password=$_POST['password'];

$sentencia=$conexion->prepare("INSERT INTO usuario (nombre,pass) VALUES (?,?)");
$sentencia->bind_param('ss',$usu_usuario,$usu_password);
$sentencia->execute();

$resultado = $sentencia->get_result();

$sentencia->close();
$conexion->close();
?>