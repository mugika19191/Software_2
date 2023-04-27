<?php
include 'conexion.php';
$usu_usuario=$_POST['usuario'];

$sentencia=$conexion->prepare("SELECT * FROM fotoPerfil WHERE usuario= ?");
$sentencia->bind_param('s',$usu_usuario);
$sentencia->execute();

$resultado = $sentencia->get_result();
$json_Data=array();
while ($fila = $resultado->fetch_assoc()) {
    $json_Data[]=$fila;
}
echo json_encode($json_Data);
$sentencia->close();
$conexion->close();
?>