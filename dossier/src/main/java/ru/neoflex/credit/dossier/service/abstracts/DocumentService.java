package ru.neoflex.credit.dossier.service.abstracts;

import ru.neoflex.credit.dossier.model.DocumentType;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface DocumentService {
    File createDocument(DocumentType documentType, Map<String, String> data);

    List<File> createAllDocuments(Long applicationID);
}
