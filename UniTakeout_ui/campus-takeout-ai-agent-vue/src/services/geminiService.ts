import { GoogleGenAI } from "@google/genai";
import { MOCK_FOOD_ITEMS } from "../constants";

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY || "" });

export const systemInstruction = `
你是一个校园外卖系统的智能助手（AIAgent）。
你的目标是帮助学生在校园食堂中选择美食并下单。

你可以访问以下菜品数据：
${JSON.stringify(MOCK_FOOD_ITEMS, null, 2)}

当用户询问推荐时：
1. 根据用户的口味偏好（如：辣、清淡、快餐等）推荐合适的菜品。
2. 每次推荐 1-3 个菜品。
3. 你的回复应包含：
   - 亲切的问候和简短的推荐理由。
   - 明确的菜品名称和价格。

你可以调用函数来生成结构化的推荐数据或订单预览。
即使你只是在文本中描述，也要确保逻辑清晰。

如果用户决定下单，请确认订单详情。
`;

export async function chatWithAI(message, history = []) {
  const chat = ai.chats.create({
    model: "gemini-3-flash-preview",
    config: {
      systemInstruction,
    },
  });

  return await chat.sendMessageStream({ message });
}
