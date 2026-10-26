package com.lab.repository.file;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.InMemoryRepository;
import com.lab.validator.Validator;

import java.io.*;

/**
 * Abstract generic base class for file persistence.
 * Implements the Template Method design pattern for converting between objects and text lines.
 *
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which must extend {@link Entity}
 */
public abstract class AbstractFileRepository<ID , E extends  Entity<ID>> extends InMemoryRepository<ID , E> {
    private final String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Extracts an entity from a formatted text line.
     *
     * @param line the text line representing the entity
     * @return the constructed entity object
     * @throws RepositoryException if the line format is invalid
     */
    protected abstract E extractEntity(String line);

    /**
     * Converts an entity into a formatted text string for file storage.
     *
     * @param entity the entity to convert
     * @return the string representation of the entity
     */
    protected abstract String createEntityAsString(E entity);

    private void loadData(){
        java.io.File file = new java.io.File(fileName);
        if(!file.exists()){
            return;
        }

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
        }catch (IOException | RepositoryException e){
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
