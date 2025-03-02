package com.example.controller; 
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.Akademik;
import com.example.model.Mahasiswa;

@Controller 
public class MahasiswaController { 
    @Autowired 
    private JdbcTemplate jdbcTemplate; 
    
    @GetMapping("/") 
    public String index(Model model) { 
        String sql = "SELECT * FROM mahasiswa WHERE status = 1 OR status IS NULL"; 
        List<Mahasiswa> mahasiswa = jdbcTemplate.query(sql, 
                BeanPropertyRowMapper.newInstance(Mahasiswa.class)); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "index"; 
    } 

    @GetMapping("/trash") 
    public String trash(Model model) { 
        String sql = "SELECT * FROM mahasiswa WHERE status = 0"; 
        List<Mahasiswa> mahasiswa = jdbcTemplate.query(sql, 
                BeanPropertyRowMapper.newInstance(Mahasiswa.class)); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "trash"; 
    }
    
    @GetMapping("/detail/{nim}")
    public String detail(@PathVariable("nim") String nim, Model model) {
        // Get mahasiswa data
        String sqlMahasiswa = "SELECT * FROM mahasiswa WHERE nim = ?";
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sqlMahasiswa, 
                BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
        model.addAttribute("mahasiswa", mahasiswa);
        
        // Get akademik data if exists
        try {
            String sqlAkademik = "SELECT * FROM akademik WHERE nim = ?";
            Akademik akademik = jdbcTemplate.queryForObject(sqlAkademik,
                    BeanPropertyRowMapper.newInstance(Akademik.class), nim);
            model.addAttribute("akademik", akademik);
        } catch (EmptyResultDataAccessException e) {
            // No akademik data found for this nim
            model.addAttribute("akademik", null);
        }
        
        return "detail";
    }

    @GetMapping("/add") 
    public String add(Model model) { 
        model.addAttribute("mahasiswa", new Mahasiswa());
        return "add"; 
    } 
 
    @PostMapping("/add") 
    public String add(Mahasiswa mahasiswa, Model model, RedirectAttributes redirectAttributes) { 
        try {
            // Check if NIM already exists
            String checkSql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, mahasiswa.getNim());
            
            if (count != null && count > 0) {
                // NIM already exists
                model.addAttribute("error", "NIM sudah terdaftar dalam database!");
                model.addAttribute("mahasiswa", mahasiswa);
                return "add";
            }
            
            // Insert new mahasiswa
            String sql = "INSERT INTO mahasiswa VALUES(?,?,?,?,1)"; 
            jdbcTemplate.update(sql, mahasiswa.getNim(), 
                    mahasiswa.getNama(), mahasiswa.getAngkatan(), mahasiswa.getGender());
            
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil ditambahkan!");
            return "redirect:/";
            
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            model.addAttribute("mahasiswa", mahasiswa);
            return "add";
        }
    }
    @GetMapping("/edit/{nim}") 
    public String edit(@PathVariable("nim") String nim, Model model) { 
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?"; 
        Mahasiswa mahasiswa = 
        jdbcTemplate.queryForObject(sql, 
        BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim); 
        model.addAttribute("mahasiswa", mahasiswa); 
        return "edit"; 
    } 
 
    @PostMapping("/edit") 
    public String edit(Mahasiswa mahasiswa, RedirectAttributes redirectAttributes) { 
        try {
            String sql = "UPDATE mahasiswa SET nama = ?, angkatan = ?, gender = ? WHERE nim = ?"; 
            jdbcTemplate.update(sql, mahasiswa.getNama(), mahasiswa.getAngkatan(), mahasiswa.getGender(), mahasiswa.getNim());
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/"; 
    }

    @GetMapping("/delete/{nim}") 
    public String delete(@PathVariable("nim") String nim, RedirectAttributes redirectAttributes) { 
        try {
            // Soft delete - update status to 0 (deleted)
            String sql = "UPDATE mahasiswa SET status = 0 WHERE nim = ?"; 
            jdbcTemplate.update(sql, nim);
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/"; 
    }
    
    @GetMapping("/restore/{nim}") 
    public String restore(@PathVariable("nim") String nim, RedirectAttributes redirectAttributes) { 
        try {
            // Restore - update status to 1 (active)
            String sql = "UPDATE mahasiswa SET status = 1 WHERE nim = ?"; 
            jdbcTemplate.update(sql, nim);
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil dipulihkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/trash"; 
    }
    
    @GetMapping("/permanent-delete/{nim}") 
    public String permanentDelete(@PathVariable("nim") String nim, RedirectAttributes redirectAttributes) { 
        try {
            // Delete akademik data first (if exists) due to foreign key constraint
            String sqlDeleteAkademik = "DELETE FROM akademik WHERE nim = ?";
            jdbcTemplate.update(sqlDeleteAkademik, nim);
            
            // Permanent delete
            String sql = "DELETE FROM mahasiswa WHERE nim = ?"; 
            jdbcTemplate.update(sql, nim);
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil dihapus permanen!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/trash"; 
    }
    
    // Akademik management methods
    @GetMapping("/add-akademik/{nim}")
    public String addAkademik(@PathVariable("nim") String nim, Model model) {
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sql,
                BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
        model.addAttribute("mahasiswa", mahasiswa);
        model.addAttribute("akademik", new Akademik());
        return "add-akademik";
    }
    
    @PostMapping("/add-akademik")
    public String addAkademik(Akademik akademik, RedirectAttributes redirectAttributes) {
        try {
            String sql = "INSERT INTO akademik (nim, ipk, semester, jurusan, fakultas) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, akademik.getNim(), akademik.getIpk(), 
                    akademik.getSemester(), akademik.getJurusan(), akademik.getFakultas());
            redirectAttributes.addFlashAttribute("success", "Data akademik berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/detail/" + akademik.getNim();
    }
    
    @GetMapping("/edit-akademik/{nim}")
    public String editAkademik(@PathVariable("nim") String nim, Model model) {
        // Get mahasiswa data
        String sqlMahasiswa = "SELECT * FROM mahasiswa WHERE nim = ?";
        Mahasiswa mahasiswa = jdbcTemplate.queryForObject(sqlMahasiswa,
                BeanPropertyRowMapper.newInstance(Mahasiswa.class), nim);
        model.addAttribute("mahasiswa", mahasiswa);
        
        // Get akademik data
        String sqlAkademik = "SELECT * FROM akademik WHERE nim = ?";
        Akademik akademik = jdbcTemplate.queryForObject(sqlAkademik,
                BeanPropertyRowMapper.newInstance(Akademik.class), nim);
        model.addAttribute("akademik", akademik);
        
        return "edit-akademik";
    }
    
    @PostMapping("/edit-akademik")
    public String editAkademik(Akademik akademik, RedirectAttributes redirectAttributes) {
        try {
            String sql = "UPDATE akademik SET ipk = ?, semester = ?, jurusan = ?, fakultas = ? WHERE id = ?";
            jdbcTemplate.update(sql, akademik.getIpk(), akademik.getSemester(), 
                    akademik.getJurusan(), akademik.getFakultas(), akademik.getId());
            redirectAttributes.addFlashAttribute("success", "Data akademik berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/detail/" + akademik.getNim();
    }
}