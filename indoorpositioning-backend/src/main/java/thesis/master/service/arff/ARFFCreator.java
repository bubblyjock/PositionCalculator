package thesis.master.service.arff;

import org.springframework.stereotype.Service;
import thesis.master.model.arff.ARFFDetails;
import thesis.master.model.core.Measurement;
import thesis.master.model.core.NavigationNode;
import thesis.master.model.core.Pattern;
import thesis.master.service.arff.load.NavigationNodesLoader;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class ARFFCreator {

    @Resource
    private NavigationNodesLoader navigationNodesLoader;

    public ARFFDetails navigationNodeIdAsClass() throws IOException {
        List<NavigationNode> navigationNodes = navigationNodesLoader.loadNavigationNodes();
        List<String> classNames = navigationNodes.stream().map(NavigationNode::getId).collect(Collectors.toList());
        return createARFFFile("navigationNodeId", navigationNodes, classNames, this::prepareNavigationNodeIdRecords);
    }

    public ARFFDetails levelAsClass() throws IOException {
        List<NavigationNode> navigationNodes = navigationNodesLoader.loadNavigationNodes();
        List<String> classNames = navigationNodes.stream().map(navigationNode -> navigationNode.getPosition().getHeight().toString()).distinct().collect(Collectors.toList());
        return createARFFFile("level", navigationNodes, classNames, this::prepareLevelRecords);
    }

    private ARFFDetails createARFFFile(String relationName, List<NavigationNode> navigationNodes, List<String> classNames, BiFunction<List<NavigationNode>, List<String>, List<String>> recordsSupplier) throws IOException {
        File arffFile = createFile(String.format("%s.arff", relationName));
        writeRelationHeader(arffFile, relationName);
        writeClassHeader(arffFile, classNames);
        List<String> attributes = writeAttributesHeader(arffFile, navigationNodes);
        writeData(arffFile, recordsSupplier.apply(navigationNodes, attributes));
        return new ARFFDetails()
                .withArffFile(arffFile)
                .withClassNames(classNames)
                .withAttributeNames(attributes);
    }

    private File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void writeRelationHeader(File arffFile, String relationName) throws IOException {
        Files.write(arffFile.toPath(), Collections.singletonList(String.format("@RELATION %s\n", relationName)));
    }

    private void writeClassHeader(File arffFile, List<String> classNames) throws IOException {
        String classNamesString = classNames.stream()
                .collect(Collectors.joining(","));
        Files.write(arffFile.toPath(), Collections.singletonList(String.format("@ATTRIBUTE class {%s}", classNamesString)), StandardOpenOption.APPEND);
    }

    private List<String> writeAttributesHeader(File arffFile, List<NavigationNode> navigationNodes) throws IOException {
        List<String> attributes = navigationNodes.stream()
                .flatMap(navigationNode -> navigationNode.getPatterns().stream())
                .flatMap(pattern -> pattern.getMeasurements().stream())
                .map(Measurement::getTransmitterId)
                .distinct()
                .collect(Collectors.toList());

        List<String> attributesHeaders = attributes.stream()
                .map(transmitterId -> String.format("@ATTRIBUTE %s NUMERIC", transmitterId))
                .collect(Collectors.toList());
        attributesHeaders.add("");
        Files.write(arffFile.toPath(), attributesHeaders, StandardOpenOption.APPEND);

        return attributes;
    }

    private void writeData(File arffFile, List<String> dataRecords) throws IOException {
        Files.write(arffFile.toPath(), Collections.singletonList("@DATA"), StandardOpenOption.APPEND);
        Files.write(arffFile.toPath(), dataRecords, StandardOpenOption.APPEND);
    }

    private List<String> prepareNavigationNodeIdRecords(List<NavigationNode> navigationNodes, List<String> attributes) {
        Map<String, List<Pattern>> navigationNodesPatterns = navigationNodes.stream()
                .collect(Collectors.toMap(NavigationNode::getId, NavigationNode::getPatterns));
        List<String> records = new ArrayList<>();

        for (Map.Entry<String, List<Pattern>> navigationNodeEntry : navigationNodesPatterns.entrySet()) {
            for (Pattern pattern : navigationNodeEntry.getValue()) {
                Map<String, Integer> measurements = pattern.getMeasurements().stream()
                        .collect(Collectors.toMap(Measurement::getTransmitterId, Measurement::getRssi));
                List<String> recordValues = new ArrayList<>();
                recordValues.add(navigationNodeEntry.getKey());
                for (String attribute : attributes) {
                    Integer rssiMeasurement = measurements.get(attribute);
                    recordValues.add(rssiMeasurement == null ? "?" : rssiMeasurement.toString());
                }
                records.add(recordValues.stream().collect(Collectors.joining(",")));
            }
        }
        return records;
    }

    private List<String> prepareLevelRecords(List<NavigationNode> navigationNodes, List<String> attributes) {
        Map<String, List<Pattern>> navigationNodesPatterns = navigationNodes.stream()
                .collect(Collectors.toMap(NavigationNode::getId, NavigationNode::getPatterns));
        Map<String, Double> navigationNodesLevel = navigationNodes.stream()
                .collect(Collectors.toMap(NavigationNode::getId, navigationNode -> navigationNode.getPosition().getHeight()));
        List<String> records = new ArrayList<>();

        for (Map.Entry<String, List<Pattern>> navigationNodeEntry : navigationNodesPatterns.entrySet()) {
            for (Pattern pattern : navigationNodeEntry.getValue()) {
                Map<String, Integer> measurements = pattern.getMeasurements().stream()
                        .collect(Collectors.toMap(Measurement::getTransmitterId, Measurement::getRssi));
                List<String> recordValues = new ArrayList<>();
                recordValues.add(navigationNodesLevel.get(navigationNodeEntry.getKey()).toString());
                for (String attribute : attributes) {
                    Integer rssiMeasurement = measurements.get(attribute);
                    recordValues.add(rssiMeasurement == null ? "?" : rssiMeasurement.toString());
                }
                records.add(recordValues.stream().collect(Collectors.joining(",")));
            }
        }
        return records;
    }

}
