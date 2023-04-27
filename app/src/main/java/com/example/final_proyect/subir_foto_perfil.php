<?php
include 'conexion.php';
$usu_usuario=$_POST['usuario'];
$foto_bit=$_POST['foto'];

$sentencia=$conexion->prepare("UPDATE fotoPerfil SET foto=? WHERE usuario=?");
$sentencia->bind_param('ss',$foto_bit,$usu_usuario);
$sentencia->execute();

$resultado = $sentencia->get_result();

$sentencia->close();
$conexion->close();
?>