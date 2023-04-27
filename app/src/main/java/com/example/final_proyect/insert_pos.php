<?php
include 'conexion.php';
$id=$_POST['id'];
$posx=$_POST['posx'];
$posy=$_POST['posy'];

$sentencia=$conexion->prepare("INSERT INTO posicion (int,posx,posy) VALUES (?,?,?)");
$sentencia->bind_param('idd',$posx,$posy);
$sentencia->execute();

$resultado = $sentencia->get_result();

$sentencia->close();
$conexion->close();
?>