import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SLAU {
    // EPSILON - погрешность при сравнении чисел
    private static final double EPSILON = 0.00000001;
    // matrix хранит в себе матрицу коэффициентов и свободных членов
    private double[][] matrix;
    // n - количество уравнений в системе
    private int n;
    // m - количество коэффициентов + 1 (свободные члены)
    private int m;
    // solutions хранит в себе решение системы (если оно единственно)
    private double[] solutions;


    // Метод isAEqualToB() проверяет два числа на равенство в пределах погрешности EPSILON
    private boolean isAEqualToB(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    // метод init читает данные из файла и заполняет matrix, n, m
    public void init(String pathToFile) throws FileNotFoundException {
        // Создаем Scanner и открываем файл по пути pathToFile на чтение. Если файла не существует, будет выброшено
        // исключение FileNotFoundException
        Scanner scanner = new Scanner(new FileInputStream(pathToFile));
        // Читаем первую строчку из файла, содержащую размеры n и m.
        readSizes(scanner);

        // Инициализируем матрицу matrix.
        matrix = new double[n][];
        // Заполняем матрицу matrix, построчно читая строки коэффициентов и свободных членов
        for (int i = 0; i < n; i++) {
            matrix[i] = readEquation(scanner);
        }

        // Закрываем файл
        scanner.close();
    }

    // приватный метод readSizes служит для упрощения чтения кода, читает n и m со Scanner при помощи регулярного
    // выражения
    private void readSizes(Scanner scanner) {
        // Читаем очередную строку из файла
        String line = scanner.nextLine();
        // Создаем регулярное выражение (Pattern), которое может распознавать целые числа
        Pattern pattern = Pattern.compile("\\d+");
        // Получаем объект matcher из Pattern, который непосредственно находит целые числа в строке line
        Matcher matcher = pattern.matcher(line);
        // Находим следующее целое число
        matcher.find();
        // Вырезаем из строки line целое число, находящееся в строке между индексами matcher.start() и matcher.end(),
        // после чего приводим вырезанную строку к типу int
        n = Integer.parseInt(line.substring(matcher.start(), matcher.end()));
        // Находим следующее целое число
        matcher.find();
        // Вырезаем из строки line целое число, находящееся в строке между индексами matcher.start() и matcher.end(),
        // после чего приводим вырезанную строку к типу int
        m = Integer.parseInt(line.substring(matcher.start(), matcher.end()));
    }

    // приватный метод readEquation служит для упрощения чтения кода, читает одну строку коэффициентов и свободных член,
    // соответствующие одному уравнение системы. Использует Scanner и регулярное выражение.
    private double[] readEquation(Scanner scanner) {
        // Читаем очередную строку из файла
        String line = scanner.nextLine();
        // Создаем регулярное выражение (Pattern), которое может распознавать дробные числа со знаком.
        // В качестве разделителя используется точка
        Pattern pattern = Pattern.compile("-?\\d+\\.?\\d*");
        // Получаем объект matcher из Pattern, который непосредственно находит дробные числа в строке line
        Matcher matcher = pattern.matcher(line);

        // Инициализируем массив equation, который будет заполнен коэффициентами и свободным членом из
        // строки файла line
        double[] equation = new double[m];
        // Находим в цикле все дробные числа строки и записываем в equation
        for (int i = 0; i < m; i++) {
            // Находим следующее дробное число
            matcher.find();
            // Вырезаем из строки line дробное число, находящееся в строке между индексами
            // matcher.start() и matcher.end(), после чего приводим вырезанную строку к типу double
            equation[i] = Double.parseDouble(line.substring(matcher.start(), matcher.end()));
        }

        return equation;
    }

    // triangleMatrix() приводит матрицу к треугольному виду и определяет, существует ли решение
    // возвращает:
    // 1 - система вырожденная
    // 2 - решений бесконечно много
    // 3 - нет решений
    // 0 - единственное решение
    public int triangleMatrix() {
        // Цикл, на каждой итерации которого выполняется очередной шаг приведения матрицы matrix к треугольному виду
        for (int i = 0; i < n - 1; i++) {
            // Находим индекс первой строки, начиная с индекса i, i-ый коэффициент которой не равен 0, для того
            // чтобы поменять строки с индексами i и swapI местами
            int swapI = findLineToSwap(i);

            // Если такой строки не найдено, то система вырожденная, прерываем цикл
            if (swapI == -1) {
                // Записываем новый статус
                return 1;
            }

            // Если i и swapI не совпадают, то есть в строке i коэффициент с индексом i равен нулю, то меняем
            // в матрице matrix местами строки с индексами i и swapI (см. описание swapI)
            if (i != swapI) {
                swapLines(i, swapI);
            }

            // Теперь, когда в строке i коэффициент с индексом i точно не равен 0, можно перейти к очередному шагу
            // приведения матрицы matrix к треугольному виду
            for (int k = i + 1; k < n; k++) {
                // На каждой итерации цикла если коэффициент с индексом i в строке k уже равен нулю, то переходим
                // к следующей итерации цикла - никаких действий не требуется
                // благодаря проверке на ноль, мы не тратим ресурсы на лишние действия
                if (isAEqualToB(matrix[k][i], 0.0)) {
                    continue;
                }

                // Вычисляем коэффициент, на которой нужно умножить строку k, чтобы i-ые коэффициент строк
                // k и i стали равными
                double factor = matrix[k][i] / matrix[i][i];
                // Умножаем строку i на коэффициент factor, результат записываем в переменную multipliedLine,
                // где хранится результат умножения. Умножение не изменяет матрицу matrix
                double[] multipliedILine = multiplyLine(matrix[i], factor);
                // Вычитаем из k-ой строки матрицы matrix строку multipliedLine. Результат записываем в матрицу matrix
                subtractLines(k, multipliedILine);
            }
        }

        // Коэффициент с индексом m - 2 последней строки матрицы matrix
        double a = matrix[n - 1][m - 2];
        // Свободный член последней строки матрицы matrix
        double b = matrix[n - 1][m - 1];

        // Если a == 0 и b == 0, то решений бесконечного много
        if (isAEqualToB(a, 0.0) && isAEqualToB(b, 0.0)) {
            return 2;
        }

        // Если a == 0 и b != 0, то решений нет
        if (isAEqualToB(a, 0.0) && !isAEqualToB(b, 0.0)) {
            return 3;
        }

        return 0;
    }

    // метод solve() запуска алгоритм решение системы
    public void solve() {
        // Инициализируем массив решений solutions. Размер m - 1, так как число решений равно числу коэффициентов
        // в каждой строке (т.е. числу неизвестных системы), а m равняется числу коэффициентов + 1 (свободный член)
        solutions = new double[m - 1];

        // Теперь мы точно знаем, что существует единственное решение системы. Запускаем обратный ход метода Гаусса.
        // Цикл на каждой итерации находит очередное неизвестное, начиная с конца
        for (int i = n - 1; i >= 0; i--) {
            // Чтобы найти очередное неизвестное, необходимо вычесть из свободного члена сумму найденных неизвестных,
            // умноженных на соответствующие им коэффициенты, и полученные результат разделить на коэффициент очередной
            // неизвестной. Для упрощения чтения кода сумма найденных неизвестных,
            // умноженных на соответствующие им коэффициенты, вычисляется в функции getSum (см. описание функции)
            solutions[i] = (matrix[i][m - 1] - getSum(i)) / matrix[i][i];
        }
    }

    // Вычисляет сумму коэффициентов, умноженных на соответствующие, найденные xi, где from <= i <= m - 2.
    // верхняя граница для i равна m - 2, потому что m - это число коэффициентов + 1 (свободный член)
    private double getSum(int from) {
        // Переменная, хранящая в себе сумму
        double sum = 0;
        // В цикле находим требуемую сумму, начиная с коэффициента с индексом from. Счетчик k останавливается на
        // m - 2, так как количество неизвестных равно m - 1.
        for (int k = from; k < m - 1; k++) {
            sum += solutions[k] * matrix[from][k];
        }

        return sum;
    }

    // меняет две строки в matrix местами
    private void swapLines(int indexA, int indexB) {
        // Запоминаем строку с индексом indexA во временной переменной tmp
        double[] tmp = matrix[indexA];
        // В строку с индексом indexA записываем строку indexB
        matrix[indexA] = matrix[indexB];
        // В строку с индексом indexB записываем запомненную строку с indexA, содержащуюся в tmp
        matrix[indexB] = tmp;
    }

    // Умножает переданную строку line на коэффициент factor. Возвращает новую строку. Результат умножения в matrix
    // не записывается
    private double[] multiplyLine(double[] line, double factor) {
        // Инициализируем массив result, который представляет строку-результат
        double[] result = new double[m];
        // В цикле умножаем каждый элемент строки line на коэффициент factor, результат размещаем в result
        for (int i = 0; i < m; i++) {
            result[i] = line[i] * factor;
        }

        return result;
    }

    // Вычитает из строки from матрицы matrix строку line поэлементно. Результат записывается в matrix
    private void subtractLines(int from, double[] line) {
        // В цикле поэлементно вычитаем из строки from матрицы matrix строку line, результат записываем в matrix
        for (int i = 0; i < m; i++) {
            matrix[from][i] = matrix[from][i] - line[i];
        }
    }

    // Находит индекс первой строки матрицы matrix, начиная с индекса iFrom, у которой коэффициент
    // с индексом iFrom не равен 0.
    private int findLineToSwap(int iFrom) {
        // Проходимся в цикле по всем строкам и как только находим ненулевой коэффициент на позиции iFrom, сразу же
        // завершаем выполнение функции и возвращаем индекс i найденной строки
        for (int i = iFrom; i < n; i++) {
            if (!isAEqualToB(matrix[i][iFrom], 0.0)) {
                return i;
            }
        }

        // Если же мы перебрали все строки и не нашли ненулевого коэффициента на позиции iFrame, возвращаем -1,
        // которая обозначает, что ненулевого коэффициента в строках, начиная с iFrom нет
        return -1;
    }

    // Представляет матрицу matrix в виде строки. Использует StringBuilder для оптимизации процесса построения строк,
    // т.к. строки в Java неизменяемы.
    public String format() {
        String result = "";
        // В цикле форматируем каждую строку матрицы matrix, используя нашу функцию formatEquation, результат
        // на каждой итерации добавляем в StringBuilder
        for (int i = 0; i < n; i++) {
            result += formatEquation(matrix[i]);
        }
        return result;
    }

    // Представляет строку equation в виде строки. Использует String.format() для форматированного вида (колонки +
    // экспоненциальный вид записи дробных чисел)
    private String formatEquation(double[] equation) {
        String result = "";
        // В цикле форматируем каждую элемент строки матрицы matrix, используя String.format() для
        // форматированного вывода, результат на каждой итерации добавляем в StringBuilder
        for (int i = 0; i < m; i++) {
            // Если число мало отличается от нуля (в пределах EPSILON), то при представлении данных,
            // считаем, что оно 0
            if (isAEqualToB(equation[i], 0.0)) {
                result += String.format("%18.6e", 0.0);
            } else {
                result += String.format("%18.6e", equation[i]);
            }
            // Если это последний элемент строки, добавляем разделитель строк.
            // Для обеспечения кроссплатформенности используем системную функцию System.lineSeparator(),
            // которая возвращает правильный разделить строк: \r\n для Windows, \n для UNIX систем.
            if (i == m - 1) {
                result += System.lineSeparator();
            }
        }

        return result;
    }

    // Возвращает строку, содержащую в себе форматированный вывод массива решений solutions.
    public String getSolutions() {
        String result = "";
        // В цикле форматируем каждую элемент массива неизвестных solutions, используя String.format() для
        // форматированного вывода, результат на каждой итерации добавляем к result
        for (int i = 0; i < m - 1; i++) {
            result += String.format("%18.6e", solutions[i]);
        }
        return result;
    }
}
