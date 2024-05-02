package uk.ac.sheffield.com1003.assignment2023.gui;

import uk.ac.sheffield.com1003.assignment2023.codeprovided.*;
import uk.ac.sheffield.com1003.assignment2023.codeprovided.gui.AbstractSpotifyDashboardPanel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SKELETON IMPLEMENTATION
 */

public class SpotifyDashboardPanel extends AbstractSpotifyDashboardPanel {

    public static final int RECORDS_TO_SHOW_IN_GUI = 1000;

    public SpotifyDashboardPanel(AbstractSongCatalog songCatalog) {
        super(songCatalog);
        filteredSongEntriesTextArea.setText(getSongEntriesSeperatedStringFromFilteredList(filteredSongEntriesList,
                RECORDS_TO_SHOW_IN_GUI));
    }

    @Override
    public void executeQuery() {
        filteredSongEntriesList = songCatalog.getSongEntriesList();
        Query query = new Query(subQueryList);
        filteredSongEntriesList = query.executeQuery(songCatalog);
        selectedArtistName = (String) comboBoxArtistNames.getSelectedItem();
        selectedAlbumName = (String) comboBoxAlbums.getSelectedItem();
        selectedSongName = (String) comboBoxSongs.getSelectedItem();
        if (!selectedArtistName.isEmpty()) {
            filteredSongEntriesList = songCatalog.getSongEntriesList(filteredSongEntriesList, SongDetail.ARTIST,
                    selectedArtistName);
        }
        if (!selectedAlbumName.isEmpty()) {
            filteredSongEntriesList = songCatalog.getSongEntriesList(filteredSongEntriesList, SongDetail.ALBUM_NAME,
                    selectedAlbumName);
        }
        if (!selectedSongName.isEmpty()) {
            filteredSongEntriesList = songCatalog.getSongEntriesList(filteredSongEntriesList, SongDetail.NAME,
                    selectedSongName);
        }
    }

    @Override
    public void clearFilters() {
        subQueryList.clear();
        subQueriesTextArea.setText("");
        executeQuery();
        filteredSongEntriesTextArea.setText(getSongEntriesSeperatedStringFromFilteredList(filteredSongEntriesList,
                RECORDS_TO_SHOW_IN_GUI));
        customChart.updateCustomChartContents(filteredSongEntriesList);
        updateStatistics();
        repaint();
    }

    @Override
    public void addFilter() {
        SongProperty songProperty = SongProperty.fromPropertyName(comboQueryProperties.getSelectedItem().toString());
        String operator = comboOperators.getSelectedItem().toString();
        double providedValue = Double.valueOf(value.getText());
        SubQuery subQuery = new SubQuery(songProperty, operator, providedValue);
        subQueryList.add(subQuery);
        subQueriesTextArea.setText(subQueryList.toString());
        executeQuery();
        filteredSongEntriesTextArea.setText(getSongEntriesSeperatedStringFromFilteredList(filteredSongEntriesList,
                RECORDS_TO_SHOW_IN_GUI));
        updateStatistics();
        populateComboBoxes();
        customChart.updateCustomChartContents(filteredSongEntriesList);
        repaint();
    }


    @Override
    public void populateComboBoxes() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBoxArtistNames.getModel();
        model.addElement("");
        for (SongEntry songEntry : filteredSongEntriesList) {
            model.addElement(songEntry.getSongArtist());
        }

        model = (DefaultComboBoxModel<String>) comboBoxAlbums.getModel();
        model.addElement("");
        for (SongEntry songEntry : filteredSongEntriesList) {
            model.addElement(songEntry.getSongAlbumName());
        }

        model = (DefaultComboBoxModel<String>) comboBoxSongs.getModel();
        model.addElement("");
        for (SongEntry songEntry : filteredSongEntriesList) {
            model.addElement(songEntry.getSongName());
        }

    }

    @Override
    public void addListeners() {
        buttonAddFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFilter();
            }
        });

        buttonClearFilters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        minCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatistics();
            }
        });

        maxCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatistics();
            }
        });

        averageCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatistics();
            }
        });

        comboBoxArtistNames.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                songDetailFilterUpdate();
            }
        });

        comboBoxAlbums.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                songDetailFilterUpdate();
            }
        });

        comboBoxSongs.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                songDetailFilterUpdate();
            }
        });

    }

    private void songDetailFilterUpdate() {
        executeQuery();
        filteredSongEntriesTextArea.setText(getSongEntriesSeperatedStringFromFilteredList(filteredSongEntriesList,
                RECORDS_TO_SHOW_IN_GUI));
        updateStatistics();
        customChart.updateCustomChartContents(filteredSongEntriesList);
        repaint();
    }

    /**
     * This method is called automatically by the Swing Framework whenever the component needs to be redrawn/repainted.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    @Override
    public void updateStatistics() {
        if (!isMinCheckBoxSelected() && !isMaxCheckBoxSelected() && !isAverageCheckBoxSelected()) {
            statisticsTextArea.setText("");
            return;
        }
        if(filteredSongEntriesList.isEmpty()) {
            statisticsTextArea.setText("");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String headLine = SongProperty.DURATION.getName() + "-" +
                SongProperty.POPULARITY.getName() + "-" +
                SongProperty.DANCEABILITY.getName() + "-" +
                SongProperty.ENERGY.getName() + "-" +
                SongProperty.LOUDNESS.getName() + "-" +
                SongProperty.SPEECHINESS.getName() + "-" +
                SongProperty.ACOUSTICNESS.getName() + "-" +
                SongProperty.INSTRUMENTALNESS.getName() + "-" +
                SongProperty.LIVENESS.getName() + "-" +
                SongProperty.VALENCE.getName() + "-" +
                SongProperty.TEMPO.getName() + "-";
        stringBuilder.append(headLine);
        stringBuilder.append("\n");
        if (isMinCheckBoxSelected()) {
            stringBuilder.append("Minimum:-");
            double minimumDuration = this.songCatalog.getMinimumValue(SongProperty.DURATION, filteredSongEntriesList);
            double minimumPopularity = this.songCatalog.getMinimumValue(SongProperty.POPULARITY,
                    filteredSongEntriesList);
            double minimumDancebility = this.songCatalog.getMinimumValue(SongProperty.DANCEABILITY,
                    filteredSongEntriesList);
            double minimumLoudness = this.songCatalog.getMinimumValue(SongProperty.LOUDNESS, filteredSongEntriesList);
            double minimumSpeechness = this.songCatalog.getMinimumValue(SongProperty.SPEECHINESS,
                    filteredSongEntriesList);
            double minimumAcousticness = this.songCatalog.getMinimumValue(SongProperty.ACOUSTICNESS,
                    filteredSongEntriesList);
            double minimumInstrumentLess = this.songCatalog.getMinimumValue(SongProperty.INSTRUMENTALNESS,
                    filteredSongEntriesList);
            double minimumLiveness = this.songCatalog.getMinimumValue(SongProperty.LIVENESS, filteredSongEntriesList);
            double minimumValence = this.songCatalog.getMinimumValue(SongProperty.VALENCE, filteredSongEntriesList);
            double minimumTempo = this.songCatalog.getMinimumValue(SongProperty.TEMPO, filteredSongEntriesList);
            stringBuilder.append(minimumDuration).append("-").append(minimumPopularity)
                    .append("-").append(minimumDancebility).append("-").append(minimumLoudness)
                    .append("-").append(minimumSpeechness).append("-").append(minimumAcousticness)
                    .append("-").append(minimumInstrumentLess).append("-").append(minimumLiveness)
                    .append("-").append(minimumValence).append("-").append(minimumTempo);
        }
        if (isMaxCheckBoxSelected()) {
            if (isMinCheckBoxSelected()) {
                stringBuilder.append("\n");
            }
            stringBuilder.append("Maximum:-");
            double maximumDuration = this.songCatalog.getMaximumValue(SongProperty.DURATION, filteredSongEntriesList);
            double maximumPopularity = this.songCatalog.getMaximumValue(SongProperty.POPULARITY,
                    filteredSongEntriesList);
            double maximumDancebility = this.songCatalog.getMaximumValue(SongProperty.DANCEABILITY,
                    filteredSongEntriesList);
            double maximumLoudness = this.songCatalog.getMaximumValue(SongProperty.LOUDNESS, filteredSongEntriesList);
            double maximumSpeechness = this.songCatalog.getMaximumValue(SongProperty.SPEECHINESS,
                    filteredSongEntriesList);
            double maximumAcousticness = this.songCatalog.getMaximumValue(SongProperty.ACOUSTICNESS,
                    filteredSongEntriesList);
            double maximumInstrumentLess = this.songCatalog.getMaximumValue(SongProperty.INSTRUMENTALNESS,
                    filteredSongEntriesList);
            double maximumLiveness = this.songCatalog.getMaximumValue(SongProperty.LIVENESS, filteredSongEntriesList);
            double maximumValence = this.songCatalog.getMaximumValue(SongProperty.VALENCE, filteredSongEntriesList);
            double maximumTempo = this.songCatalog.getMaximumValue(SongProperty.TEMPO, filteredSongEntriesList);
            stringBuilder.append(maximumDuration).append("-").append(maximumPopularity)
                    .append("-").append(maximumDancebility).append("-").append(maximumLoudness)
                    .append("-").append(maximumSpeechness).append("-").append(maximumAcousticness)
                    .append("-").append(maximumInstrumentLess).append("-").append(maximumLiveness)
                    .append("-").append(maximumValence).append("-").append(maximumTempo);
        }
        if (isAverageCheckBoxSelected()) {
            if (isMinCheckBoxSelected() || isMaxCheckBoxSelected()) {
                stringBuilder.append("\n");
            }
            stringBuilder.append("Mean:-");
            double averageDuration = this.songCatalog.getAverageValue(SongProperty.DURATION, filteredSongEntriesList);
            double averagePopularity = this.songCatalog.getAverageValue(SongProperty.POPULARITY,
                    filteredSongEntriesList);
            double averageDancebility = this.songCatalog.getAverageValue(SongProperty.DANCEABILITY,
                    filteredSongEntriesList);
            double averageLoudness = this.songCatalog.getAverageValue(SongProperty.LOUDNESS, filteredSongEntriesList);
            double averageSpeechness = this.songCatalog.getAverageValue(SongProperty.SPEECHINESS,
                    filteredSongEntriesList);
            double averageAcousticness = this.songCatalog.getAverageValue(SongProperty.ACOUSTICNESS,
                    filteredSongEntriesList);
            double averageInstrumentLess = this.songCatalog.getAverageValue(SongProperty.INSTRUMENTALNESS,
                    filteredSongEntriesList);
            double averageLiveness = this.songCatalog.getAverageValue(SongProperty.LIVENESS, filteredSongEntriesList);
            double averageValence = this.songCatalog.getAverageValue(SongProperty.VALENCE, filteredSongEntriesList);
            double averageTempo = this.songCatalog.getAverageValue(SongProperty.TEMPO, filteredSongEntriesList);
            stringBuilder.append(averageDuration).append("-").append(averagePopularity)
                    .append("-").append(averageDancebility).append("-").append(averageLoudness)
                    .append("-").append(averageSpeechness).append("-").append(averageAcousticness)
                    .append("-").append(averageInstrumentLess).append("-").append(averageLiveness)
                    .append("-").append(averageValence).append("-").append(averageTempo);
        }
        stringBuilder.append("\n");
        if(filteredSongEntriesList.size() > RECORDS_TO_SHOW_IN_GUI) {
            stringBuilder.append("Displaying " + RECORDS_TO_SHOW_IN_GUI + " from total number of filtered records:" + filteredSongEntriesList.size());
        } else {
            stringBuilder.append("Displaying total filtered records:" + filteredSongEntriesList.size());
        }

        statisticsTextArea.setText(stringBuilder.toString());
    }


    @Override
    public boolean isMinCheckBoxSelected() {
        return minCheckBox.isSelected();
    }

    @Override
    public boolean isMaxCheckBoxSelected() {
        return maxCheckBox.isSelected();
    }

    @Override
    public boolean isAverageCheckBoxSelected() {
        return averageCheckBox.isSelected();
    }


    protected String getSongEntriesSeperatedStringFromFilteredList(List<SongEntry> filteredSongEntriesList, int totalRecordsToShow) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        StringBuilder headLine = new StringBuilder();
        headLine.append("ID").append("-").append("Name Of the track").append("-")
                .append("Name of the artist").append("-").append("Name of the album of the track")
                .append("-").append(SongProperty.DURATION.getName()).append("-")
                .append(SongProperty.POPULARITY.getName()).append("-")
                .append(SongProperty.DANCEABILITY.getName()).append("-")
                .append(SongProperty.ENERGY.getName()).append("-")
                .append(SongProperty.LOUDNESS.getName()).append("-")
                .append(SongProperty.SPEECHINESS.getName()).append("-")
                .append(SongProperty.ACOUSTICNESS.getName()).append("-")
                .append(SongProperty.INSTRUMENTALNESS.getName()).append("-")
                .append(SongProperty.LIVENESS.getName()).append("-")
                .append(SongProperty.VALENCE.getName()).append("-")
                .append(SongProperty.TEMPO.getName()).append("-");
        stringBuilder.append(headLine.toString());
        stringBuilder.append("\n");
        for (SongEntry songEntry : filteredSongEntriesList) {
            stringBuilder.append(songEntry.getId())
                    .append("-").append(songEntry.getSongName())
                    .append("-").append(songEntry.getSongArtist())
                    .append("-").append(songEntry.getSongAlbumName())
                    .append("-").append(songEntry.getSongProperty(SongProperty.DURATION))
                    .append("-").append(songEntry.getSongProperty(SongProperty.POPULARITY))
                    .append("-").append(songEntry.getSongProperty(SongProperty.DANCEABILITY))
                    .append("-").append(songEntry.getSongProperty(SongProperty.ENERGY))
                    .append("-").append(songEntry.getSongProperty(SongProperty.LOUDNESS))
                    .append("-").append(songEntry.getSongProperty(SongProperty.SPEECHINESS))
                    .append("-").append(songEntry.getSongProperty(SongProperty.ACOUSTICNESS))
                    .append("-").append(songEntry.getSongProperty(SongProperty.INSTRUMENTALNESS))
                    .append("-").append(songEntry.getSongProperty(SongProperty.LIVENESS))
                    .append("-").append(songEntry.getSongProperty(SongProperty.VALENCE))
                    .append("-").append(songEntry.getSongProperty(SongProperty.TEMPO));
            stringBuilder.append("\n");
            if (count == totalRecordsToShow) {
                stringBuilder.append("Displaying" + totalRecordsToShow + " records from total of " +
                        filteredSongEntriesList.size());
                break;
            }
            count++;
        }
        return stringBuilder.toString();
    }
}
