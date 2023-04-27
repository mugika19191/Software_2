<?php
include 'conexion.php';
$usu_usuario=$_POST['usuario'];

$sentencia=$conexion->prepare("INSERT INTO fotoPerfil (usuario,foto) VALUES (?,'')");
$sentencia->bind_param('s',$usu_usuario);
$sentencia->execute();

$resultado = $sentencia->get_result();

$sentencia->close();
$conexion->close();
?>