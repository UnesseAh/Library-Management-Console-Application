package service;

import repository.CopyRepository;

import java.sql.SQLException;

public class CopyService {
    private CopyRepository copyRepository;
    public CopyService(CopyRepository copyRepository){
        this.copyRepository = copyRepository;
    }

    public int getAvailableCopyId (String isbn) throws SQLException {
        return copyRepository.getAvailableCopyId(isbn);
    }
}
