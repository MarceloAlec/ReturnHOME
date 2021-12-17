<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT");

require_once '../../config/connection.php' ;
require_once '../../models/pet.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);
$pet = new Pet();

$stmt=$pet->updateStatusMissing($pdo, $data["id"], filter_var($data["missing"], FILTER_VALIDATE_BOOLEAN));

if($stmt){
    //ESTADO ACTUALIZADO
    http_response_code(200);
    echo json_encode(array("message"=>"Pet has been updated successfully"));
}
else{
    //CLIENTE NO AUTORIZADO
    http_response_code(304);
    echo json_encode(array("message" => "No pet found "));
}  
?>
