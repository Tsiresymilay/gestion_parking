package com.example.parking.controller.rest;

import com.example.parking.dto.request.SpaceRequest;
import com.example.parking.dto.response.SpaceResponse;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import com.example.parking.service.SpaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parking/spaces")
public class SpaceRestController {

    private final SpaceService spaceService;

    public SpaceRestController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping
    public ResponseEntity<SpaceResponse> create(@Valid @RequestBody SpaceRequest request) {
        SpaceResponse created = spaceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<SpaceResponse> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) SpaceStatus status,
                                    @RequestParam(required = false) SpaceType type,
                                    @RequestParam(required = false) String zone,
                                    @PageableDefault(size = 10, sort = "number") Pageable pageable) {
        return spaceService.search(keyword, status, type, zone, pageable);
    }

    @GetMapping("/available")
    public List<SpaceResponse> available() {
        return spaceService.findAvailable();
    }

    @GetMapping("/{id}")
    public SpaceResponse get(@PathVariable Long id) {
        return spaceService.findById(id);
    }

    @PutMapping("/{id}")
    public SpaceResponse update(@PathVariable Long id, @Valid @RequestBody SpaceRequest request) {
        return spaceService.update(id, request);
    }

    @PutMapping("/{id}/status")
    public SpaceResponse changeStatus(@PathVariable Long id, @RequestParam SpaceStatus status) {
        return spaceService.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        spaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
