package haui.csn.thitot.service;
import haui.csn.thitot.entity.Question;


import haui.csn.thitot.entity.Exam;
import haui.csn.thitot.entity.Result;
import haui.csn.thitot.entity.Subject;
import haui.csn.thitot.entity.User;
import haui.csn.thitot.repository.ExamRepository;
import haui.csn.thitot.repository.QuestionRepository;
import haui.csn.thitot.repository.ResultRepository;
import haui.csn.thitot.repository.SubjectRepository;
import haui.csn.thitot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ExamService {
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private UserRepository userRepository;

    public Exam getRandomExamBySubjectAndQuestions(Integer subjectId, int questionCount) {
        return examRepository.findRandomExamBySubjectAndQuestions(subjectId, questionCount);
    }

    public List<Exam> getExamByCreateBy() {
        return examRepository.findAllByTeacherRole();
    }
    public Exam getExamById(Integer examId) {
        return examRepository.findById(examId).get();
    }
    public Exam getExamByResultId(Integer resultId) {
        return examRepository.findExamByResultId(resultId);
    }
    public List<Exam> getAll() {
        return examRepository.findAll();
    }
    @Transactional
    public void addQuestionsToExam(Integer examId, List<Integer> questionIds) {
        for (Integer qId : questionIds) {
            examRepository.insert(examId, qId);
        }
    }
    public void save(Exam exam) {
        examRepository.save(exam);
    }

    public Exam getExamBySubject(Integer subjectId) {
        return examRepository.findFirstBySubject_SubjectId(subjectId);
    }
    // Phương thức mới: Lấy đề thi theo ID giáo viên
    public List<Exam> getExamsByTeacher(Integer teacherId) {
        // Phương thức này cần được định nghĩa trong ExamRepository
        return examRepository.findByCreatedBy_Id(teacherId);
    }
    public List<Exam> searchBySubjectAndKeyword(Integer teacherId, Integer subjectId,String keyword) {
        return examRepository.findByCreatedBy_IdAndSubject_SubjectIdAndExamNameContainingIgnoreCase(teacherId, subjectId, keyword);
    }
    // Case 2: Chỉ Lọc theo Môn học
    public List<Exam> getExamsByTeacherAndSubject(Integer teacherId, Integer subjectId) {
        return examRepository.findByCreatedBy_IdAndSubject_SubjectId(teacherId, subjectId);
    }

    // Case 3: Chỉ Tìm kiếm theo Từ khóa
    public List<Exam> searchByTeacherAndKeyword(Integer teacherId, String keyword) {
        return examRepository.findByCreatedBy_IdAndExamNameContainingIgnoreCase(teacherId, keyword);
    }
    public Exam createExam(String examName, Integer duration,Integer totalQuestions, Integer subjectId, User createdBy, String description) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Exam exam = new Exam();
        exam.setExamName(examName); // Dùng tên trường mới
        exam.setDuration(duration);
        exam.setTotalQuestions(totalQuestions);// Dùng tên trường mới
        exam.setSubject(subject);
        exam.setCreatedBy(createdBy);
        exam.setDescription(description); // Set description

        return examRepository.save(exam);
    }

    public Exam findById(Integer id) {
        return examRepository.findById(id).orElse(null);
    }


    @Transactional
    public void updateExamQuestions(Integer examId, List<Integer> selectedQuestionIds) {

        //Reset trạng thái cũ (Hủy gán hết các câu cũ của đề này)
        questionRepository.removeQuestionsFromExam(examId);

        int count = 0;

        //gán danh sách mới (nếu có chọn)
        if (selectedQuestionIds != null && !selectedQuestionIds.isEmpty()) {

            questionRepository.assignQuestionsToExam(examId, selectedQuestionIds);
            count = selectedQuestionIds.size();
        }

        //Cập nhật số lượng câu hỏi cho đề thi
        examRepository.updateTotalQuestions(examId, count);

    }
    public Exam updateExamDetails(Integer examId, String examName, Integer duration,Integer totalQuestions, String description) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));

        exam.setExamName(examName);
        exam.setDuration(duration);
        exam.setTotalQuestions(totalQuestions);
        exam.setDescription(description);

        return examRepository.save(exam);
    }

    public boolean isExamEmpty(Integer examId) {
        // Sử dụng Repository để đếm số câu hỏi đang gán cho đề thi này
        List<Integer> questionIds = questionRepository.findQuestionIdsByExamId(examId);
        return questionIds == null || questionIds.isEmpty();
    }

    @Transactional
    public void deleteExam(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // 1. XÓA LỊCH SỬ KẾT QUẢ (Thay vì set null)
        resultRepository.deleteByExam_ExamId(examId);

        // 2. GỠ LIÊN KẾT CÂU HỎI (Giữ lại câu hỏi cho ngân hàng)
        questionRepository.removeQuestionsFromExam(examId);

        // 3. XÓA ĐỀ THI
        examRepository.deleteById(examId);
    }
    public Set<Integer> getSelectedQuestionIds(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElse(null);

        if (exam == null || exam.getQuestions() == null) {
            return java.util.Collections.emptySet();
        }

        return exam.getQuestions().stream()
                .map(Question::getQuestionId)
                .collect(java.util.stream.Collectors.toSet());
    }
    // 1. Lấy danh sách kết quả chi tiết của một đề thi
    public List<Result> getResultsByExamId(Integer examId) {

        return resultRepository.findByExam_ExamId(examId);
    }
    // 2. Tính Điểm Trung Bình
    public Double getAverageScore(Integer examId) {

        return resultRepository.findAverageScoreByExamId(examId);
    }

    public Long countTotalAttempts(Integer examId) {
        return resultRepository.countByExam_ExamId(examId);
    }
    public ByteArrayInputStream exportToExcel(List<Result> results, String examName) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ket Qua Thi");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Sinh Viên", "Lớp", "Điểm Số","Thời gian bắt đầu", "Thời Gian Nộp"};


            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            CellStyle decimalStyle = workbook.createCellStyle();
            decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }


            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Result result : results) {
                Row row = sheet.createRow(rowIdx++);

                String studentName = (result.getUser() != null) ? result.getUser().getFull_name() : "Đã xóa";
                row.createCell(0).setCellValue(studentName);

                String className = (result.getUser() != null) ? result.getUser().getClassName() : "N/A";
                row.createCell(1).setCellValue(className);


                Cell scoreCell = row.createCell(2);
                if (result.getScore() != null) {
                    scoreCell.setCellValue(result.getScore());
                } else {
                    scoreCell.setCellValue(0);
                }
                scoreCell.setCellStyle(decimalStyle);

                if (result.getStartTime() != null) {
                    row.createCell(3).setCellValue(result.getStartTime().format(formatter));
                } else {
                    row.createCell(3).setCellValue("Chưa nộp");
                }

                if (result.getEndTime() != null) {
                    row.createCell(4).setCellValue(result.getEndTime().format(formatter));
                } else {
                    row.createCell(4).setCellValue("Chưa nộp");
                }
            }

            for(int i=0; i<columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
}
