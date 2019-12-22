package home.gabe.tvword.services;

import home.gabe.tvword.model.Display;
import home.gabe.tvword.model.web.ModifyDisplayCommand;

import java.util.Set;

public interface DisplayService {

    //Display authenticate(String name, String password );

    Display findById(Long id);

    Set<Display> findAll(boolean showDeleted);

    Display register(String name, String note, String password);

    Display update(ModifyDisplayCommand command);

    void delete(Display display);

}
