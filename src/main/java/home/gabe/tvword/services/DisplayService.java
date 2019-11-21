package home.gabe.tvword.services;

import home.gabe.tvword.model.Display;

import java.util.Set;

public interface DisplayService {

    //Display authenticate(String name, String password );

    Display findById(Long id);

    Set<Display> findAll();

    Display register(String name, String note, String password);

    void delete(Display display);
}
