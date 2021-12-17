<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/connection.php' ;
require_once '../../models/pet.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
// OBTENER VALOR DEL PARAMETRO ID
$action = isset($_GET["action"]) ? $_GET["action"] : die();
$id = isset($_GET["id"]) ? $_GET["id"] : die();

$pet = new Pet();

switch($action){
    case 1:
        $stmt = $pet->readPetByClient($pdo,$id); 
        break;
    case 2:
        $stmt = $pet->readPetById($pdo,$id); 
        break;
    case 3:
        $stmt = $pet->readMissingPet($pdo); 
        break;
    default:
        $stmt = false;
}


if($stmt){
    //EXISTEN MASCOTAS
    http_response_code(200);

    switch($action){
        case 1:
            echo json_encode(array("message" => "Already have pets registered", "pets" => $stmt)); 
            break;
        case 2:
            echo json_encode(array("message" => "Already have pets registered", "pet" => $stmt["0"], "client" => $stmt["1"]));
            break;
        case 3:
            echo json_encode(array("message" => "Missing Pets", "pets" => $stmt));
            break;
    
    }

}
else{
    //NO SE ENCONTRARON MASCOTAS REGISTRADAS
    http_response_code(404);
    echo json_encode(array("message" => "No pets found"));
}








?>