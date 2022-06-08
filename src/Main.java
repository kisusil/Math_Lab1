import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        // Создаем объект класса SLAU
        SLAU slau = new SLAU();

        // Вызываем метод init с переданным именем файла для заполнения матрицы matrix из файла.
        // Если из метода init выброшено исключение FileNotFoundException, значит файла с таким
        // именем нет, выводим сообщение и завершаем программу.
        try {
            slau.init("data.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Файла не существует");
            return;
        }

        // Выводим пояснительное сообщение
        System.out.println("Матрица, прочитанная из файла: ");
        // Вызываем метод format() класса SLAU, который представляет матрицу в виде форматированной строки
        // (см. описание метода format() класса SLAU). Результат печатаем на экран
        System.out.println(slau.format());

        // Выводим пояснительное сообщение
        System.out.println("Треугольная матрица:");
        // Находим и печатаем треугольную матрицу.
        int code = slau.triangleMatrix();
        System.out.println(slau.format());

        // Если solutions содержит статус-строку, выводим ее на экран
        if (code > 0) {
            switch (code) {
                case 1:
                    System.out.println("Система вырожденная");
                    break;
                case 2:
                    System.out.println("Решений бесконечно много");
                    break;
                case 3:
                    System.out.println("Нет решений");
                    break;
            }
        } else {
            // Вызываем метод solve() класса SLAU, который запускает алгоритм Гаусса. Результаты работы алгоритма
            // содержатся в полях класса SLAU
            slau.solve();
            // Получаем строку, в которой содержится либо сообщение, что решений нет или их бесконечно много, либо
            // форматированное представление массива найденных неизвестных
            String solutions = slau.getSolutions();

            // Выводим пояснительное сообщение
            System.out.println("Решение:");
            // Выводим на экран форматированную строку с найденными неизвестными
            System.out.println(solutions);
        }
    }
}
