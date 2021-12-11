<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/connection.php' ;
require_once '../../models/pet.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
// OBTENER VALOR DEL PARAMETRO ID
$byIdClient = isset($_GET['byIdClient']) ? $_GET['byIdClient'] : die();
$byIdClient = filter_var($byIdClient, FILTER_VALIDATE_BOOLEAN);

$id = isset($_GET['id']) ? $_GET['id'] : die();

$pet = new Pet();

if($byIdClient){
    $stmt = $pet->readPet($pdo,$id); 
}
else{
    $stmt = $pet->readSinglePet($pdo,$id); 
}

if($stmt){
    //EXISTEN MASCOTAS
    if($byIdClient){
        http_response_code(200);
        echo json_encode(array("message" => "Already have pets registered", "pets" => $stmt));
    }
    else{
        http_response_code(200);
        echo json_encode(array("message" => "Already have pets registered", "pet" => $stmt));
    }
}
else{
    //NO SE ENCONTRARON MASCOTAS REGISTRADAS
    http_response_code(404);
    echo json_encode(array("message" => "No pets found"));

}





?>