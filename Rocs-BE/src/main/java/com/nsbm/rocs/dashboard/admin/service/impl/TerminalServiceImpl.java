package com.nsbm.rocs.dashboard.admin.service.impl;

import com.nsbm.rocs.dashboard.admin.service.TerminalService;
import com.nsbm.rocs.entity.main.Terminal;
import com.nsbm.rocs.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminalServiceImpl implements TerminalService {

    private final TerminalRepository terminalRepository;

    @Autowired
    public TerminalServiceImpl(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @Override
    public List<Terminal> getAllTerminals() {
        return terminalRepository.findAll();
    }

    @Override
    public Terminal addTerminal(Terminal terminal) {
        // You might want to check for duplicate code here if not handled by DB
        // exception
        return terminalRepository.save(terminal);
    }

    @Override
    public Terminal updateTerminal(Long id, Terminal terminalDetails) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found"));

        terminal.setName(terminalDetails.getName());
        terminal.setTerminalCode(terminalDetails.getTerminalCode());
        terminal.setBranchId(terminalDetails.getBranchId());
        // Do not update isActive, createdAt here usually

        return terminalRepository.save(terminal);
    }

    @Override
    public void deleteTerminal(Long id) {
        terminalRepository.deleteById(id);
    }

    @Override
    public void toggleTerminalStatus(Long id) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found"));

        terminal.setIsActive(!terminal.getIsActive());
        terminalRepository.save(terminal);
    }

    @Override
    public Terminal getTerminalById(Long id) {
        return terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found"));
    }

    @Override
    public List<Terminal> searchTerminals(String query) {
        String q = query.toLowerCase();
        return terminalRepository.findAll().stream()
                .filter(t -> (t.getName() != null && t.getName().toLowerCase().contains(q)) ||
                        (t.getTerminalCode() != null && t.getTerminalCode().toLowerCase().contains(q)))
                .toList();
    }
}
