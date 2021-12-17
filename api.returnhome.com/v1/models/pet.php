<?php

class Pet{

    private $tblPet = "tblpet";
    private $tblClient = "tblclient";

    public function __construct(){

    }


    public function createPet($connection, $name, $breed, $gender, $description, $idClient){

        $query = "INSERT INTO ".$this->tblPet . "(name, breed, gender, description, id_client) values(?, ?, ?, ?, ?)";

        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$name);
        $stmt->bindValue(2,$breed);
        $stmt->bindValue(3,$gender);
        $stmt->bindValue(4,$description);
        $stmt->bindValue(5,$idClient);
        
        if($stmt->execute()){
            $id=$connection->lastInsertId();
            return array("id"=>$id);
        }
        else{
            return false;
        }

        
    }

    public function readPetByClient($connection, $idClient){
        $query = "SELECT idPet, name, breed, gender, description, id_client FROM " . $this->tblPet . " WHERE id_client = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$idClient);
        $stmt->execute();
        $pets_num= $stmt->rowCount();

        if($pets_num > 0){

            $pets_array = array();
        
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                extract($row);
                $e = array("id" => $idPet,
                           "name" => $name,
                           "breed" => $breed,
                           "gender" => $gender,
                           "description" => $description,
                           "id_client" => $id_client);
        
                array_push($pets_array, $e);
            }
            return $pets_array;
        }
        
        else{
           return false;
        }
    }

    public function readPetById($connection, $idPet){

        $query = "SELECT *, tblclient.name AS client_name, tblpet.name AS pet_name, tblclient.gender AS client_gender, tblpet.gender AS pet_gender 
                  FROM " . $this->tblClient . 
                  " INNER JOIN tblpet 
                  on tblclient.id = tblpet.id_client  
                  WHERE idPet = ?";
       
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$idPet);
        $stmt->execute();
        $pets_num= $stmt->rowCount();

        if($pets_num == 1){

            $result = $stmt->fetch(PDO::FETCH_ASSOC);

            
            $pet = array("id" => $result["idPet"],
                         "name" => $result["pet_name"],
                         "breed" => $result["breed"],
                         "gender" => $result["pet_gender"],
                         "description" => $result["description"],
                         "missing" => filter_var($result["isMissing"], FILTER_VALIDATE_BOOLEAN),
                         "id_client" => $result["id_client"]);

            $client = array("id" => $result["id"],
                            "name" => $result["client_name"],
                            "email" => $result["email"],
                            "gender" => $result["client_gender"],
                            "phoneNumber" => $result["phoneNumber"]);

            return array($pet, $client);
        }
        else{
           return false;
        }
    }

    public function readMissingPet($connection){
        $query = "SELECT idPet, name, breed, gender, description, id_client FROM " . $this->tblPet . " WHERE isMissing = true";
        $stmt = $connection->prepare($query);
        $stmt->execute();
        $missing_pets_num= $stmt->rowCount();

        if($missing_pets_num > 0){

            $pets_array = array();
        
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                extract($row);
                $e = array("id" => $idPet,
                           "name" => $name,
                           "breed" => $breed,
                           "gender" => $gender,
                           "description" => $description,
                           "id_client" => $id_client);
        
                array_push($pets_array, $e);
            }
            return $pets_array;
        }
        
        else{
           return false;
        }
    }

    public function deletePet($connection, $id){
        $query = "DELETE FROM " . $this->tblPet . " WHERE idPet = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$id);
        $stmt->execute();
        $rows_affected = $stmt->rowCount();
        if($rows_affected==1){
            return true;
        }
        else{
            return false;
        }
    }

    public function updatePet($connection, $id, $name, $breed, $gender, $description){
        $query = "UPDATE " . $this->tblPet . " SET name = ?,breed = ?,gender = ?,description = ? WHERE idPet = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$name);
        $stmt->bindValue(2,$breed);
        $stmt->bindValue(3,$gender);
        $stmt->bindValue(4,$description);
        $stmt->bindValue(5,$id);
        $stmt->execute();
        $rows_affected = $stmt->rowCount();
        if($rows_affected==1){
            return true;
        }
        else{
            return false;
        }
    }

    public function updateStatusMissing($connection, $id, $missing){
        $query = "UPDATE " . $this->tblPet . " SET isMissing = ? WHERE idPet = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$missing);
        $stmt->bindValue(2,$id);
        $stmt->execute();
        $rows_affected = $stmt->rowCount();
        
        if($rows_affected==1){   
            return true;
        }
        else{
            return false;
        }
    }




    





}



?>