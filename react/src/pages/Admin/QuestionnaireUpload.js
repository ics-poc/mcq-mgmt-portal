import { useMemo, useState } from 'react';
import {
    Paper, TextField, Button, Stack, Typography, TableContainer, Table, TableHead, TableRow,
    TableCell, TableBody, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import DownloadIcon from '@mui/icons-material/Download';

export default function QuestionnaireUpload({ setToast }) {
    const [subjectName, setSubjectName] = useState('');
    const [file, setFile] = useState(null);
    const [uploads, setUploads] = useState([]);

    const canUpload = useMemo(() => subjectName.trim() && file, [subjectName, file]);

    const onFilePick = (e) => {
        const f = e.target.files?.[0];
        if (f && f.type !== 'application/pdf') {
            setToast('Please upload a PDF file');
            e.target.value = '';
            return;
        }
        setFile(f || null);
    };

    const onUpload = () => {
        if (!canUpload) return;
        setUploads(prev => [{ id: crypto.randomUUID(), subjectName: subjectName.trim(), fileName: file.name, uploadedAt: Date.now(), blobUrl: URL.createObjectURL(file) }, ...prev]);
        setSubjectName('');
        setFile(null);
        setToast('Mock upload successful! This will be processed and appear in the "Review Questionnaire" tab after backend processing.');
    };

    const onDeleteUpload = (id) => setUploads(prev => prev.filter(u => u.id !== id));

    return (
        <Paper sx={{ p: 3 }}>
            <Typography variant="h6">Upload Assessment Category PDF</Typography>
            <Stack spacing={2} sx={{ mt: 2, maxWidth: 640 }}>
                <TextField label="Category Name" value={subjectName} onChange={(e) => setSubjectName(e.target.value)} required />
                <Stack direction="row" spacing={2} alignItems="center">
                    <Button variant="outlined" component="label" startIcon={<UploadFileIcon />}>Choose PDF<input hidden type="file" accept="application/pdf" onChange={onFilePick} /></Button>
                    <Typography variant="body2">{file ? file.name : 'No file selected'}</Typography>
                    <Button variant="contained" onClick={onUpload} disabled={!canUpload}>Upload</Button>
                </Stack>
                <Typography variant="subtitle1" sx={{ mt: 3 }}>Available Category PDFs</Typography>
                <TableContainer>
                    <Table size="small">
                        <TableHead><TableRow><TableCell>Category</TableCell><TableCell>File</TableCell><TableCell>Uploaded At</TableCell><TableCell align="right">Actions</TableCell></TableRow></TableHead>
                        <TableBody>
                            {uploads.length === 0 && <TableRow><TableCell colSpan={4}>No Category PDFs uploaded</TableCell></TableRow>}
                            {uploads.map((u) => (
                                <TableRow key={u.id}>
                                    <TableCell>{u.subjectName}</TableCell><TableCell>{u.fileName}</TableCell><TableCell>{new Date(u.uploadedAt).toLocaleString()}</TableCell>
                                    <TableCell align="right">
                                        <IconButton size="small" component="a" href={u.blobUrl} download={u.fileName}><DownloadIcon /></IconButton>
                                        <IconButton size="small" color="error" onClick={() => onDeleteUpload(u.id)}><DeleteIcon /></IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Stack>
        </Paper>
    );
}