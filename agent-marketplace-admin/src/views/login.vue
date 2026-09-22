<template>
  <div class="login">
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="off">
      <div class="login-brand"><span>PARALLEL DIGITAL · OFFICIAL SITE</span><h3 class="title">{{ title }}</h3><p>内容发布 · 商务登记 · 运营维护</p></div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          size="large"
          autocomplete="off"
          placeholder="账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          autocomplete="off"
          placeholder="密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          size="large"
          autocomplete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0 0 25px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from "@/utils/jsencrypt"
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
  code: "",
  uuid: ""
})

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
// 验证码开关
const captchaEnabled = ref(true)
// 注册开关
const register = ref(false)
const redirect = ref(undefined)

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 })
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 })
        Cookies.set("rememberMe", "true", { expires: 30 })
      } else {
        clearRememberedLogin()
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur]
          }
          return acc
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

function clearRememberedLogin() {
  Cookies.remove("username")
  Cookies.remove("password")
  Cookies.remove("rememberMe")
}

function getRememberedLogin() {
  if (Cookies.get("rememberMe") !== "true") {
    clearRememberedLogin()
    return
  }
  const username = Cookies.get("username")
  const password = Cookies.get("password")
  if (!username || !password) {
    clearRememberedLogin()
    return
  }
  loginForm.value.username = username
  loginForm.value.password = decrypt(password)
  loginForm.value.rememberMe = true
}

getCode()
getRememberedLogin()
</script>

<style lang='scss' scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  overflow: hidden;
  position: relative;
  background:
    radial-gradient(circle at 78% 28%, rgba(45, 158, 230, 0.2), transparent 28%),
    linear-gradient(135deg, #f8fbff 0%, #edf6ff 46%, #dceeff 100%);
}
.login::before {
  content: "";
  position: absolute;
  width: 720px;
  height: 720px;
  right: -190px;
  bottom: -300px;
  border: 1px solid rgba(20, 119, 210, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 0 80px rgba(20, 119, 210, 0.035), 0 0 0 180px rgba(20, 119, 210, 0.025);
}
.title {
  margin: 8px 0 6px;
  color: #102a4c;
  font-size: 25px;
  letter-spacing: 0.5px;
}

.login-form {
  border: 1px solid rgba(31, 124, 213, 0.16);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 24px 70px rgba(26, 89, 148, 0.18);
  width: 440px;
  padding: 34px 38px 18px;
  z-index: 1;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
  :deep(.el-button--primary) {
    height: 44px;
    border-color: #087bd8;
    background: #087bd8;
    font-weight: 700;
    letter-spacing: 4px;
  }
}
.login-brand {
  margin-bottom: 28px;
  span {
    color: #087bd8;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 1.4px;
  }
  p {
    margin: 0;
    color: #7388a1;
    font-size: 13px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.login-code-img {
  height: 40px;
  padding-left: 12px;
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #67809d;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

html.dark .login {
  background: #10233e;
  .login-form {
    background: var(--el-bg-color-overlay) !important;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5);
  }
}
</style>
