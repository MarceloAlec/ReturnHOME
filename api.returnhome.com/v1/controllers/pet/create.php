<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

require_once '../../config/connection.php' ;
require_once '../../models/pet.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

//PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);

$pet = new Pet();
$stmt=$pet->createPet($pdo, $data["name"], $data["breed"], $data["gender"], $data["description"], $data["id_client"]);

if($stmt){
    //CLIENTE CREADO
    http_response_code(201);
    echo json_encode(array("message"=>"Pet has been created successfully","pet" => $stmt));
}
else{
    //CLIENTE NO AUTORIZADO
    http_response_code(401);
    echo json_encode(array("message" => "Pet already exists"));
}
    
?>


