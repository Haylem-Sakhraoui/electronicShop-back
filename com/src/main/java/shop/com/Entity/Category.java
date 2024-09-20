package shop.com.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
public enum Category {
    CABLES , LUMIERE, VIDEO_PROJECTEUR ,MATERIEL_SONO ,RECEPTEUR,ELECTRONIQUES_GENERALES

}
