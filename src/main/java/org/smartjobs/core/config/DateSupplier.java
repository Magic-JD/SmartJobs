package org.smartjobs.core.config;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class DateSupplier {

    public Date getDate(){
        return Date.valueOf(LocalDate.now());
    }

}
