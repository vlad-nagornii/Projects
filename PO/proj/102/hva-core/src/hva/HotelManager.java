package hva;

import hva.exceptions.*;
import hva.visitor.NormalSatisfaction;

import java.io.*;

/**
 * Class that represents the hotel application.
 */
public class HotelManager {

    /** Name of the file storing the current Hotel. */
    private String _filename = "";

    /** This is the current hotel. */
    private Hotel _hotel = new Hotel();

    /**
     * Saves the serialized application's state into the file associated to the current network.
     *
     * @throws FileNotFoundException if for some reason the file cannot be created or opened.
     * @throws MissingFileAssociationException if the current network does not have a file.
     * @throws IOException if there is some error while serializing the state of the network to disk.
     */
    public void save() throws FileNotFoundException, MissingFileAssociationException, IOException {
        if(!hasChanged()) return;
        if (_filename == null || _filename.isBlank())
            throw new MissingFileAssociationException();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                                      new BufferedOutputStream(
                                      new FileOutputStream(_filename)))) {
            oos.writeObject(_hotel);
            _hotel.setChanged(false);   
        }
    }
    
    /**
     * Saves the serialized application's state into the file associated to the current network.
     *
     * @throws FileNotFoundException if for some reason the file cannot be created or opened.
     * @throws MissingFileAssociationException if the current network does not have a file.
     * @throws IOException if there is some error while serializing the state of the network to disk.
     */
    public void saveAs(String filename) throws FileNotFoundException, MissingFileAssociationException, IOException {
        _filename = filename;
        save();
    }

    /**
     * @param filename name of the file containing the serialized application's state
     *        to load.
     * @throws UnavailableFileException if the specified file does not exist or there is
     *         an error while processing this file.
     */
    public void load(String filename) throws UnavailableFileException {
        try{
            ObjectInputStream ois = new ObjectInputStream(
                                     new BufferedInputStream(
                                     new FileInputStream(filename)));
            _filename = filename;
            _hotel = (Hotel) ois.readObject();
            _hotel.setChanged(false);
        } catch (IOException | ClassNotFoundException e) {
            throw new UnavailableFileException(filename);
        }
    }

    /**
     * Read text input file.
     *
     * @param filename name of the text input file
     * @throws ImportFileException
     */
    public void importFile(String filename) throws ImportFileException {
        _hotel.importFile(filename);
    }

    /**
     * @return true if the hotel has changed since the last save.
     */
    public boolean hasChanged() { 
        return _hotel.getChanged();
    }

    /**
     * @return current hotel.
     */
    public Hotel getHotel() { 
        return _hotel;
    }

    /**
     * Reset the hotel.
     */
    public void reset() {
        _hotel = new Hotel();
        _filename = null;
    }

    /**
     * Advances the season in the hotel
     */
    public int advanceSeason(){
        return _hotel.advanceSeason();
    }

    /**
     * Returns the satisfaction of the hotel.
     * 
     * @return the satisfaction of the hotel.
     */
    public double showGlobalSatisfaction() {
        return _hotel.accept(new NormalSatisfaction());
    }
}
