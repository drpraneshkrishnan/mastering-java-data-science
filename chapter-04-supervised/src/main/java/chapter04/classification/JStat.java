package chapter04.classification;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import chapter04.cv.Dataset;
import chapter04.cv.Fold;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import smile.validation.AUC;

public class JStat {

    public static DescriptiveStatistics crossValidate(List<Fold> folds, Function<Dataset, Classifier> trainer) {
        double[] aucs = folds.parallelStream().mapToDouble(fold -> {
            Dataset foldTrain = fold.getTrain();
            Dataset foldValidation = fold.getTest();
            Classifier model = trainer.apply(foldTrain);
            return auc(model, foldValidation);
        }).toArray();

        return new DescriptiveStatistics(aucs);
    }

    public static double auc(Classifier model, Dataset dataset) {
        double[] probability = predict(model, dataset);
        int[] truth = dataset.getYAsInt();
        return AUC.measure(truth, probability);
    }

    public static double[] predict(Classifier model, Dataset dataset) {
        double[][] X = dataset.getX();
        double[] result = new double[X.length];

        for (int i = 0; i < X.length; i++) {
            DenseVector vector = new DenseVector(X[i]);
            DataPoint point = new DataPoint(vector);
            CategoricalResults out = model.classify(point);
            result[i] = out.getProb(1);
        }

        return result;
    }
}