<?php
include 'conexion.php';
$usu_usuario=$_POST['usuario'];
$foto_bit=$_POST['foto'];
$posx=$_POST['posx'];
$posy=$_POST['posy'];

$sentencia=$conexion->prepare("INSERT INTO fotos (usuario,foto,posx,posy) VALUES (?,?,?,?)");
$sentencia->bind_param('ssss',$usu_usuario,$foto_bit,$posx,$posy);
$sentencia->execute();

$resultado = $sentencia->get_result();

$sentencia->close();
$conexion->close();
?>