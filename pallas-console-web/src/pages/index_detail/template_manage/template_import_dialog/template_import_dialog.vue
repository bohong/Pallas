<template>
    <el-dialog :title="templateImportTitle" size="small" v-model="isTemplateImportVisible" :before-close="closeDialog">
        <el-form :model="importInfo" :rules="rules" ref="importInfo" label-width="120px">
            <el-row>
                <el-col :span="18">
                    <div class="template-import-upload">
                        <el-upload
                        ref="upload"
                        :action="templateImportUrl"
                        :auto-upload="false"
                        :data="importInfo"
                        :multiple="false"
                        :file-list="fileList"
                        :on-remove="handleRemove"
                        :on-error="handleError"
                        :on-success="handleSuccess">
                            <el-button size="small" type="primary" @click="handleUpload">点击上传</el-button>
                            <div slot="tip" class="el-upload__tip">请上传zip文件，且不超过10Mb</div>
                        </el-upload>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="18">
                    <el-form-item label="变更描述" prop="updateDesc">
                        <el-input v-model="importInfo.updateDesc"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">    
            <el-button @click="closeDialog()">取 消</el-button>
            <el-button type="confirm" @click="submitImportTemplate">确定</el-button>
        </div>
    </el-dialog>
</template>

<script>
export default {
  props: ['indexId', 'templateImportTitle', 'templateImportUrl'],
  data() {
    return {
      isTemplateImportVisible: true,
      rules: {
        updateDesc: [{ required: true, message: '请输入变更描述', trigger: 'blur' }],
      },
      fileList: [],
      importInfo: {
        updateDesc: '',
      },
    };
  },
  methods: {
    handleUpload() {
      this.$refs.upload.clearFiles();
      this.$refs.upload.$refs['upload-inner'].$refs.input.value = '';
    },
    handleRemove() {
      this.$refs.upload.$refs['upload-inner'].$refs.input.value = '';
    },
    handleError(err) {
      this.$message.errorMessage(`上传失败: ${err}`);
    },
    handleSuccess(response) {
      if (response.status === 200) {
        this.$message.successMessage('导入模板成功！', () => {
          this.$emit('close-submit-dialog');
        });
      } else {
        this.$message.errorMessage(response.description);
      }
    },
    submitImportTemplate() {
      this.$refs.importInfo.validate((valid) => {
        if (valid) {
          this.$refs.upload.submit();
        }
      });
    },
    closeDialog() {
      this.$emit('close-dialog');
      this.$refs.upload.clearFiles();
      this.$refs.upload.$refs['upload-inner'].$refs.input.value = '';
    },
  },
};

</script>

<style type="text/css">
.template-import-upload {
  margin: 20px 40px;
}
</style>
