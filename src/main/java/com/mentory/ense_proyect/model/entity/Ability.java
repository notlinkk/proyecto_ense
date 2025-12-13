package com.mentory.ense_proyect.model.entity;


import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "abilities")
/*
    *
 */
public class Ability  {

        @Id String name;
        String description;

        @ManyToMany(mappedBy = "abilities")
        Set <Lesson> lessons;

        public Ability() {}
        public Ability( String name, String description) {
                this.name = name;
                this.description = description;
        }
        
        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }
        

}
