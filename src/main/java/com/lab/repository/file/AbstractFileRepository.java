package com.lab.repository.file;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.InMemoryRepository;
import com.lab.validator.Validator;

import java.io.*;

public abstract class AbstractFileRepository<ID , E extends  Entity<ID>> extends InMemoryRepository<ID , E> {
    private final String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    protected abstract E extractEntity(String line);

    protected abstract String createEntityAsString(E entity);

    private void loadData(){
        int lineNr = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line  = reader.readLine()) != null){
                lineNr++;
                if(line.isBlank())
                    continue;
                E entity = extractEntity(line);
                super.save(entity);
            }
        }catch(FileNotFoundException e){
            throw new RepositoryException("The file " +fileName + " does not exist");
        }catch (IOException e){
            throw new RepositoryException("Error while reading the file " + fileName  + " at line " + lineNr, e);
        }
    }

    private void writeToFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            for(E entity : findAll()){
                writer.write(createEntityAsString(entity));
                writer.newLine();
            }
        }catch (IOException e){
            throw new RepositoryException("Error while writing in the file " + fileName , e);
        }
    }

    @Override
    public void delete(ID id) {
        super.delete(id);
        writeToFile();
    }

    @Override
    public void save(E entity) {
        super.save(entity);
        writeToFile();
    }
}
