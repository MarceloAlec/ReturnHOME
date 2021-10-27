<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/connection.php' ;
require_once '../../models/pet.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
// OBTENER VALOR DEL PARAMETRO ID
$id = isset($_GET['id']) ? $_GET['id'] : die();

$pet = new Pet();
$stmt = $pet->deletePet($pdo,$id);

if($stmt){
    //MASCOTA ELIMINADA
    http_response_code(200);
    echo json_encode(array("message" => "Your pet was successfully deleted"));
}
else{
    //NO SE PUDO COMPLETAR EL PROCESO
    http_response_code(404);
    echo json_encode(array("message" => "No pet found"));

}





?>