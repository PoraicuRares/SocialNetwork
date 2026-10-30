package com.lab.repository.file;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.InMemoryRepository;
import com.lab.validator.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FileRepository<ID ,E extends Entity<ID>> extends InMemoryRepository<ID , E> {
    private final Path path;
    private final Function<String , E> lineToEntity;
    private final Function<E, String> entityToLine;

    public FileRepository(Validator<E> validator, String fileName, Function<String, E> lineToEntity, Function<E, String> entityToLine) {
        super(validator);
        this.path = Path.of(fileName);
        this.lineToEntity = lineToEntity;
        this.entityToLine = entityToLine;
        loadData();
    }

    private void loadData(){
        if(Files.notExists(path)){
            return;
        }
        List<String> lines;
        try(Stream<String> fileLines = Files.lines(path)){
            lines = fileLines.collect(Collectors.toList());
        }catch (IOException e){
            throw new RepositoryException("Error while reading the file " + path , e);
        }
        IntStream.range(0 , lines.size())
                .filter(i -> !lines.get(i).isBlank())
                .forEach(i -> loadLine(lines.get(i) , i + 1));

    }

    private void loadLine(String line , int lineNr){
        try{
            super.save(lineToEntity.apply(line));
        }catch (RuntimeException e){
            throw new RepositoryException("Error while reading the file " +path + " at line "  + lineNr, e);
        }
    }

    private void writeToFile(){
        List<String> lines = stream()
                .map(entityToLine)
                .collect(Collectors.toList());
        try{
            Files.write(path  ,lines);
        }catch (IOException e){
            throw  new RepositoryException("Error while reading the file " + path , e);
        }
    }

    @Override
    public void save(E entity) {
        super.save(entity);
        writeToFile();
    }

    @Override
    public void delete(ID id) {
        super.delete(id);
        writeToFile();
    }
}
