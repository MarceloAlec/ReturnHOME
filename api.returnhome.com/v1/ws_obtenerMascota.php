<?php
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";


if(isset($_GET["opcion"]) && isset($_GET["id"])){

    $opcion = $_GET["opcion"];
    $id = $_GET["id"];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
        
    switch($opcion){
        case 1:
        
            $stmt = $conexion->prepare("SELECT * FROM tblmascota WHERE idCliente = ?");
            $stmt->bindValue(1,$id);

            if($stmt->execute()){

                $array_mascotas = array();
            
                while ($fila = $stmt->fetch(PDO::FETCH_ASSOC)){
                    extract($fila);
                    $e = array("idMascota" => $idMascota,
                                "nombre" => $nombre,
                                "raza" => $raza,
                                "genero" => $genero,
                                "descripcion" => $descripcion,
                                "idCliente" => $idCliente);
            
                    array_push($array_mascotas, $e);
                }

                $stmt=null;
                http_response_code(200);
                echo json_encode(array("mascotas" => $array_mascotas)); 
            }
            else{
                http_response_code(404);
            }
            break;

        case 2:
            $consulta = "SELECT *, tblcliente.nombre AS nombreCliente, tblmascota.nombre AS nombreMascota, tblmascota.genero AS generoMascota
                        FROM tblcliente
                        INNER JOIN tblmascota on tblcliente.idCliente = tblmascota.idCliente 
                        WHERE idMascota = ?";

            $stmt = $conexion->prepare($consulta);
            $stmt->bindValue(1,$id);

            if($stmt->execute()){

                $resultado = $stmt->fetch(PDO::FETCH_ASSOC);
                $stmt=null;

                $mascota = array("idMascota" => $resultado["idMascota"],
                                 "nombre" => $resultado["nombreMascota"],
                                 "raza" => $resultado["raza"],
                                 "genero" => $resultado["generoMascota"],
                                 "descripcion" => $resultado["descripcion"],
                                 "desaparecida" => filter_var($resultado["desaparecida"], FILTER_VALIDATE_BOOLEAN),
                                 "idCliente" => $resultado["idCliente"]);

                $cliente = array("idCliente" => $resultado["idCliente"],
                                 "nombre" => $resultado["nombreCliente"],
                                 "email" => $resultado["email"],
                                 "numeroCelular" => $resultado["numeroCelular"]);

                http_response_code(200);
                echo json_encode(array("mascota" => $mascota, "cliente" => $cliente));
            
            }
            else{
                http_response_code(404);
            }

            break;
        case 3:
            
            $stmt = $conexion->prepare("SELECT * FROM tblmascota WHERE desaparecida = true");

            if($stmt->execute()){

                $array_mascotas = array();
            
                while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    extract($row);
                    $e = array("idMascota" => $idMascota,
                               "nombre" => $nombre,
                               "raza" => $raza,
                               "genero" => $genero,
                               "descripcion" => $descripcion,
                               "idCliente" => $idCliente);
                
                    array_push($array_mascotas, $e);
                }
                $stmt=null;
                http_response_code(200);
                echo json_encode(array("mascotas" => $array_mascotas));
            }
            else{
                http_response_code(404);
            }
            break;
        default:
            http_response_code(404);
    }
}
else{
    http_response_code(404);
}

?>