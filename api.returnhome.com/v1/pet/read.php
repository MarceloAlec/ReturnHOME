<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once '../connection.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
// OBTENER VALOR DEL PARAMETRO ID

$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);

$sqlQuery = "select idPet, name, breed, gender, description from tblpet where id_client = ?";
$stmt = $pdo->prepare($sqlQuery);
$stmt->bindValue(1,$data["id"]);
$stmt->execute();

$petCount = $stmt->rowCount();

if($petCount > 0){
        
    $petsArray = array();
    $petsArray["pets"] = array();

    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        extract($row);
        $e = array(
            "idPet" => $idPet,
            "name" => $name,
            "breed" => $breed,
            "gender" => $gender,
            "description" => $description,
        );

        array_push($petsArray["pets"], $e);
    }
    echo json_encode($petsArray);
}

else{
    http_response_code(404);
    echo json_encode(
        array("message" => "No pets found.")
    );
}

?>