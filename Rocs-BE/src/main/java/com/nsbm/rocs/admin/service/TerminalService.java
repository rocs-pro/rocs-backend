package com.nsbm.rocs.admin.service;

import com.nsbm.rocs.entity.main.Terminal;
import java.util.List;

public interface TerminalService {
    List<Terminal> getAllTerminals();

    Terminal addTerminal(Terminal terminal);

    Terminal updateTerminal(Long id, Terminal terminal);

    void deleteTerminal(Long id);

    void toggleTerminalStatus(Long id);

    Terminal getTerminalById(Long id);

    List<Terminal> searchTerminals(String query);
}
